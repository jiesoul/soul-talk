(ns soul-talk.login
  (:require [domina :as dom]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :as ajax]
            [soul-talk.auth-validate :refer [login-errors]]
            [taoensso.timbre :as log]
            [soul-talk.components.common :as c]
            [reagent.session :as session]
            [reagent.core :as r]))


(defn login! [login-data errors]
  (reset! errors (login-errors @login-data))
  (when-not @errors
    (ajax/POST "/login"
               {:format        :json
                :headers       {"Accept" "application/transit+json"}
                :params        @login-data
                :handler       #(do
                                  (reset! login-data {})
                                  (set! (.. js/window -location -href) "/dash"))
                :error-handler #(reset! errors {:server-error (get-in % [:response :message])})
                :response-format :json, :keywords? true})))

(defn login-component []
  (let [login-data (r/atom {})
        errors (r/atom nil)]
    (fn []
      [c/modal
       "loginModal"
       "登录"
       [:div
        [c/text-input "Email" :email "Email Address" login-data]
        (when-let [error (first (:email @errors))]
          [:div.alert.alert-danger error])
        [c/password-input "密码" :password "输入密码" login-data]
        (when-let [error (first (:password @errors))]
          [:div.alert.alert-danger error])
        (when-let [error (:server-error @errors)]
          [:div.alert.alert-danger error])]
       [:div
        [:input.btn.btn-primary
         {:type     :submit
          :value    "登录"
          :on-click #(login! login-data errors)}]]])))

(defn login-button []
  [:a.nav-link.p-2
   {:data-toggle "modal"
    :data-target "#loginModal"}
   "登录"])



