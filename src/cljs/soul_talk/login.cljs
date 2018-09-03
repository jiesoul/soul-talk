(ns soul-talk.login
  (:require [domina :refer [by-id value by-class destroy!]]
            [domina.events :refer [listen!]]
            [reagent.core :as reagent :refer [atom]]))

(defn validate-form []
  (let [email (by-id "email")
        password (by-id "password")]
    (if (and (> (count (value email)) 0)
             (> (count (value password)) 0))
      true
      (do
        (js/alert "email和密码不能为空")
        false))))

(defn login-component []
  [:form#loginForm.form-signin
   {:action "/login" :method "post"}
   [:h1.h3.mb-3.font-weight-normal "Please sign in"]
   [:label.sr-only "email" "email"]
   [:input#email.form-control
    {:type "text" :auto-focus true :placeholder "Email Address"}]
   ])

(reagent/render
  [login-component]
  (by-id "content"))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (let [login-form (by-id "loginForm")]
      (set! (.-onsubmit login-form) validate-form))))