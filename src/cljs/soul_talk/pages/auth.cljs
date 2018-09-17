(ns soul-talk.pages.auth
  (:require [reagent.core :as r]
            [soul-talk.pages.common :as c]
            [soul-talk.auth-validate :refer [login-errors]]
            [taoensso.timbre :as log]
            [re-frame.core :refer [dispatch subscribe]]))

(defn login-page []
  (r/with-let
    [login-data (r/atom nil)
     error     (subscribe [:error])
     on-close (fn []
                (reset! login-data nil)
                (dispatch [:clean-error]))]
    [c/modal
     {:show true
      :on-hide on-close}
     "Soul Talk Login"
     [:div
      [c/text-input "Email" :email "Email Address" login-data]
      [c/password-input "密码" :password "输入密码" login-data]
      (when @error
        [:div.alert.alert-danger @error])]
     [:div
      [:a.btn.btn-secondary.m-2
       {:on-click #(dispatch [:set-active-page :register])}
       "Register"]
      [:a.btn.btn-primary
       {:value    "Login"
        :on-click #(dispatch [:login @login-data])}
       "Login"]]]))


(defn register-page []
  (r/with-let
    [reg-data (r/atom nil)
     error (subscribe [:error])]
    [c/modal
     "regModal"
     "Soul Talk Register"
     [:div
      [:div.well.well-sm "* 为必填"]
      [c/text-input "Email" :email "enter a email" reg-data]
      [c/password-input "密码" :password "输入密码最少8位" reg-data]
      [c/password-input "确认密码" :pass-confirm "确认密码和上面一样" reg-data]
      (when @error
        [:div.alert.alert-message @error])]
     [:div
      [:input.btn.btn-primary.btn-block
       {:type     :submit
        :value    "Register"
        :on-click #(dispatch [:register @reg-data])}]]]))