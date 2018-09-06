(ns soul-talk.register
  (:require [domina :as dom]
            [reagent.core :as reagent :refer [atom]]
            [soul-talk.auth-validate :as validate]
            [ajax.core :as ajax]
            [reagent.session :as session]
            [taoensso.timbre :as log]))

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
        [:h1.h3.mb-3.font-weight-normal.text-center "注册"]
        [:div.form-group
         [:label "邮箱"]
         [:input#email.form-control
          {:type        "text"
           :name        "email"
           :auto-focus  true
           :placeholder "xx@xx.xx"
           :on-change     (fn [e]
                          (let [d (.. e -target)]
                            (swap! reg-data assoc :email (.-value d))
                            (validate-invalid d validate/validate-email)))
           :value (:email @reg-data)}]
         [:div.invalid-feedback "无效的 Email"]]
        [:div.form-group
         [:label "密码"]
         [:input#password.form-control
          {:type        "password"
           :name        "password"
           :placeholder "密码"
           :on-change     (fn [e]
                          (let [d (.-target e)]
                            (swap! reg-data assoc :password (.-value d))
                            (validate-invalid d validate/validate-passoword)))
           :value (:password @reg-data)}]
         [:div.invalid-feedback "无效的密码"]]
        [:div.form-group
         [:label "重复密码"]
         [:input#pass-confirm.form-control
          {:type        "password"
           :name        "pass-confirm"
           :placeholder "重复密码"
           :on-change     (fn [e]
                            (let [d (.-target e)]
                              (swap! reg-data assoc :pass-confirm (.-value d))
                              (validate-invalid d validate/validate-passoword)))
           :value (:pass-confirm @reg-data)}]
         [:div.invalid-feedback "无效的密码"]]
        (when-not [error (:client-error @error)]
          [:div#error.alert.alert-danger error])
        (when-not [error (:server-error @error)]
          [:div#error.alert.alert-danger error])
        [:input#submit.btn.btn-primary.btn-lg.btn-block
         {:type     :submit
          :value    "保存"
          :on-click #(register! reg-data error)}]
        [:p.mt-5.mb-3.text-muted "&copy @2018"]]])))


(defn load-page []
  (reagent/render
    [register-component]
    (dom/by-id "app")))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (load-page)))