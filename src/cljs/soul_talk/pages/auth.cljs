(ns soul-talk.pages.auth
  (:require [reagent.core :as r]
            [soul-talk.pages.common :as c]
            [soul-talk.auth-validate :refer [login-errors]]
            [taoensso.timbre :as log]
            [re-frame.core :refer [dispatch subscribe]])
  (:import goog.History))

(defn login-page []
  (let  [login-data (r/atom {:email ""
                             :password ""})
         error     (subscribe [:error])
         email (r/cursor login-data [:email])
         password (r/cursor login-data [:password])]
    [:div.text-center.container
     [:div.form-signin
      [:h1.h3.mb-3.font-weight-normal.text-center "Login"]
      [:label.sr-only
       {:for "email"}]
      [:input#email.form-control
       {:type :text
        :placeholder "请输入Email"
        :required true
        :auto-focus true
        :on-change #(reset! email (-> % .-target .-value))}]
      [:label.sr-only
       {:for "password"}]
      [:input#password.form-control
       {:type :password
        :placeholder "请输入密码"
        :required true
        :on-change #(reset! password (-> % .-target .-value))}]
      (when @error
        [:div.alert.alert-danger @error])
      [:button.btn.btn-lg.btn-primary.btn-block
       {:type    "submit"
        :on-click #(dispatch [:login @login-data])}
       "Login"]]]))

(defn register-page []
  (r/with-let
    [reg-data (r/atom nil)
     error (subscribe [:error])
     email (r/cursor reg-data [:email])
     password (r/cursor reg-data [:password])
     pass-confirm (r/cursor reg-data [:pass-confirm])]
    [:div.text-center.container
     [:div.form-signin
      [:h1.h3.mb-3.font-weight-normal.text-center "Register"]
      [:label.sr-only
       {:for "email"}]
      [:input#email.form-control
       {:type :text
        :placeholder "请输入Email"
        :required true
        :auto-focus true
        :on-change #(reset! email (-> % .-target .-value))}]
      [:label.sr-only
       {:for "password"}]
      [:input#password.form-control
       {:type :password
        :placeholder "请输入密码"
        :required true
        :on-change #(reset! password (-> % .-target .-value))}]
      [:label.sr-only
       {:for "pass-confirm"}]
      [:input#pass-confirm.form-control
       {:type :password
        :placeholder "请再次输入密码"
        :required true
        :on-change #(reset! pass-confirm (-> % .-target .-value))}]
      (when @error
        [:div.alert.alert-danger @error])
      [:button.btn.btn-lg.btn-primary.btn-block
       {:type    "submit"
        :on-click #(dispatch [:register @reg-data])}
       "Register"]]]))