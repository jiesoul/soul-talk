(ns soul-talk.user
  (:require [soul-talk.components.common :as c]
            [reagent.core :as r]
            [soul-talk.auth-validate :refer [change-pass-errors login-errors reg-errors]]
            [ajax.core :as ajax]
            [taoensso.timbre :as log]
            [reagent.session :as session]
            [domina :as dom]))

(defn change-password! [pass-data errors]
  (reset! errors (change-pass-errors @pass-data))
  (when-not @errors
    (ajax/POST
      "/change-pass"
      {:format       :json
       :headers      {"Accept" "application/transit+json"}
       :params       @pass-data
       :handler      #(js/alert "保存成功")
       :error-handler #(do
                        (log/error %)
                        (reset! errors {:server-error (get-in % [:response "message"])}))})))

(defn change-pass-form []
  (let [pass-data (r/atom {:email js/identity})
        errors (r/atom nil)]
    (fn []
      [:div.container-fluid
       [:div.form-signin
        [:h1.h3.mb-3.font-weight-normal.text-center "修改密码"]
        [:div
         [:div.well.well-sm "* 为必填"]
         [c/password-input "旧密码" :pass-old "输入密码最少8位" pass-data]
         (when-let [error (first (:pass-old @errors))]
           [:div.alert.alert-danger error])
         [c/password-input "新密码" :pass-new "输入密码最少8位" pass-data]
         (when-let [error (first (:pass-new @errors))]
           [:div.alert.alert-danger error])
         [c/password-input "确认密码" :pass-confirm "确认密码和上面一样" pass-data]
         (when-let [error (first (:pass-confirm @errors))]
           [:div.alert.alert-danger error])
         (when-let [error (:server-error @errors)]
           [:div.alert.alert-danger error])]
        [:div
         [:input.btn.btn-primary.btn-block
          {:type     :submit
           :value    "保存"
           :on-click #(change-password! pass-data errors)}]]]])))

