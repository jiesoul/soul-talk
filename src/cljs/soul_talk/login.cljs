(ns soul-talk.login
  (:require [domina :as dom]
            [reagent.core :as reagent :refer [atom]]
            [ajax.core :as ajax]
            [soul-talk.auth-validate :refer [login-errors]]
            [taoensso.timbre :as log]
            [soul-talk.components.common :as c]
            [reagent.session :as session]))


(defn login! [login-data errors]
  (reset! errors (login-errors @login-data))
  (when-not @errors
    (ajax/POST "/login"
               {:format        :json
                :headers       {"Accept" "application/transit+json"}
                :params        @login-data
                :handler       #(set! (.. js/window -location -href) "/dash")
                :error-handler #(reset! errors {:server-error (get-in % [:response "message"])})})))

(defn login-component []
  (let [login-data (atom {})
        errors (atom {})]
    (fn []
      [c/modal
       [:div "登录"]
       [:div
        [c/text-input "Email" :email "Email Address" login-data]
        (when-let [error (first (:email @errors))]
          [:div.alert.alert-danger error])
        [c/password-input "密码" :password "输入密码" login-data]
        (when-let [error (first (:password @errors))]
          [:div.alert.alert-danger error])
        (when-let [error (:server-error @errors)]
          [:div.alert.alert-danger error])
        ]
       [:div
        [:input#submit.btn.btn-primary
         {:type     :submit
          :value    "登录"
          :on-click #(login! login-data errors)}]
        [:input#submit.btn.btn-primary
         {:type     :button
          :value    "注册"
          :on-click #(set! (.. js/window -location -href) "/register")}]]])))

(defn login-button []
  [:a.btn
   {:on-click #(login-component)}
   "登录"])

(defn load-page []
  (reagent/render
    [login-component]
    (dom/by-id "app")))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (load-page)))