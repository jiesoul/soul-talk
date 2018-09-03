(ns soul-talk.login
  (:require [domina :as dom]
            [domina.events :as ev]
            [reagent.core :as reagent :refer [atom]]
            [soul-talk.auth-validate :refer [validate-email validate-passoword]]
            [ajax.core :as ajax]))

(defn validate-invalid [input-id vali-fun]
  (if-not (vali-fun (dom/value input-id))
    (dom/add-class! input-id "is-invalid")
    (dom/remove-class! input-id "is-invalid")))

(defn validate-form []
  (let [email (dom/by-id "email")
        password (dom/by-id "password")]
    (if (and (validate-email (dom/value email))
             (validate-passoword (dom/value password)))
      true
      (do
        (js/alert "email和密码不能为空")
        false))))


(defn login-component []
  [:form#loginForm.form-signin
   [:h1.h3.mb-3.font-weight-normal.text-center "Please sign in"]
   [:div.form-group
    [:label "Email address"]
    [:input#email.form-control
     {:type       "text"
      :name "email"
      :auto-focus true
      :placeholder "Email Address"
      :on-blur #(validate-invalid (dom/by-id "email") validate-email)}]
    [:div.invalid-feedback "无效的 Email"]]
   [:div.form-group
    [:label "Password"]
    [:input#password.form-control
     {:type "password"
      :name "password"
      :placeholder "password"
      :on-blur #(validate-invalid (dom/by-id "password") validate-passoword)}]
    [:div.invalid-feedback "无效的密码"]]
   [:div.form-group.form-check
    [:input#rememeber.form-check-input {:type "checkbox"}]
    [:label "记住我"]]
   [:div#error]
   [:input#submit.btn.btn-lg.btn-primary.btn-block {:type "submit" :value "登录"}]
   [:p.mt-5.mb-3.text-muted
    "&copy @2018"]])

(reagent/render
  [login-component]
  (dom/by-id "content"))

(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str status status-text)))

(defn login! []
  (.log js/console "commit")
  (ajax/POST
    "/login"
    {:format :json
     :headers {"Accept" "application/transit+json"}
     :params {:email (dom/value (dom/by-id "email"))
              :password (dom/value (dom/by-id "password"))}
     :handler handler
     :error-handler error-handler}))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (let [login-form (dom/by-id "submit")]
      (ev/listen! login-form :click #(when (validate-form) login!)))))