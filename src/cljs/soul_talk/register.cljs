(ns soul-talk.register
  (:require [domina :as dom]
            [reagent.core :as reagent :refer [atom]]
            [soul-talk.auth-validate :as validate]
            [ajax.core :as ajax]
            [reagent.session :as session]
            [taoensso.timbre :as log]
            [soul-talk.components.common :as c])
  (:import [goog.history Html5History]))

(defn validate-invalid [input vali-fun]
  (if-not (vali-fun (.-value input))
    (dom/add-class! input "is-invalid")
    (dom/remove-class! input "is-invalid")))

(defn register! [reg-date errors]
  (reset! errors (validate/reg-errors @reg-date))
  (if-not @errors
    (ajax/POST "/register"
               {:format        :json
                :headers       {"Accept" "application/transit+json"}
                :params        @reg-date
                :handler       #(do
                                  (session/put! :identity (:email @reg-date))
                                  (reset! reg-date {})
                                  (js/alert "注册成功")
                                  (set! (.. js/window -location -href) "/login"))
                :error-handler #(reset!
                                  errors
                                  {:server-error (get-in % [:response "message"])})})
    (let [error (vals @errors)
          msg   (ffirst error)]
      (js/alert msg))))

(defn register-component []
  (let [reg-data (atom {})
        error (atom nil)]
    (fn []
      [:div.container
       [:div#loginForm.form-signin
        [:h1.h3.mb-3.font-weight-normal.text-center "Soul Talk"]
        [:div
         [:div.well.well-sm "* 为必填"]
         [c/text-input "Email" :email "enter a email, EX: example@xx.com" reg-data]
         [c/password-input "密码" :password "输入密码最少8位" reg-data]
         [c/password-input "确认密码" :pass-confirm "确认密码和上面一样" reg-data]
         (when-let [error (:server-error @error)]
           [:div.alert.alert-danger error])]
        [:div
         [:input.btn.btn-primary.btn-block
          {:type     :submit
           :value    "注册"
           :on-click #(register! reg-data error)}]
         [:input.btn.btn-primary.btn-block
          {:type     :submit
           :value    "登录"
           :on-click #(set! (.. js/window -location -href) "/login")}]]
        [:p.mt-5.mb-3.text-muted "&copy @2018"]]])))


(defn load-page []
  (reagent/render
    [register-component]
    (dom/by-id "app")))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (load-page)))