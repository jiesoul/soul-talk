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
      (if @error
        [:div.alert.alert-danger @error])]
     [:div
      [:a.btn.btn-secondary.m-2
       "Register"]
      [:a.btn.btn-primary
       {:value    "Login"
        :on-click #(dispatch [:login @login-data])}
       "Login"]]]))