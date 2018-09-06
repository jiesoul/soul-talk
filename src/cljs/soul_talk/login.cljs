(ns soul-talk.login
  (:require [domina :as dom]
            [reagent.core :as reagent :refer [atom]]
            [soul-talk.auth-validate :as validate]
            [ajax.core :as ajax]
            [soul-talk.auth-validate :refer [login-errors]]
            [taoensso.timbre :as log]))

(defn validate-invalid [input vali-fun]
  (if-not (vali-fun (.-value input))
    (dom/add-class! input "is-invalid")
    (dom/remove-class! input "is-invalid")))

(defn handler-ok [response]
  (.log js/console (str "response: " response)))

(defn handler-error [{:keys [status response]}]
  (.log js/console (get response "message")))

(defn login! [login-data errors]
  (reset! errors (login-errors @login-data))
  (if-not @errors
    (ajax/POST "/login"
               {:format        :json
                :headers       {"Accept" "application/transit+json"}
                :params        @login-data
                :handler       handler-ok
                :error-handler handler-error})
    (let [error (vals @errors)]
      (log/error error)
      (js/alert error))))

(defn login-component []
  (let [login-data (atom {})
        errors (atom {})]
    (fn []
      [:div.container
       [:div#loginForm.form-signin
        [:h1.h3.mb-3.font-weight-normal.text-center "Please sign in"]
        [:div.form-group
         [:label "Email address"]
         [:input#email.form-control
          {:type        "text"
           :name        "email"
           :auto-focus  true
           :placeholder "Email Address"
           :on-change     (fn [e]
                          (let [d (.. e -target)]
                            (swap! login-data assoc :email (.-value d))
                            (validate-invalid d validate/validate-email)))
           :value (:email @login-data)}]
         [:div.invalid-feedback "无效的 Email"]]
        [:div.form-group
         [:label "Password"]
         [:input#password.form-control
          {:type        "password"
           :name        "password"
           :placeholder "password"
           :on-change     (fn [e]
                          (let [d (.-target e)]
                            (swap! login-data assoc :password (.-value d))
                            (validate-invalid d validate/validate-passoword)))
           :value (:password @login-data)}]
         [:div.invalid-feedback "无效的密码"]]
        [:div.form-group.form-check
         [:input#remameber.form-check-input {:type "checkbox"}]
         [:label "记住我"]]
        [:div#error @errors]
        [:input#submit.btn.btn-primary
         {:type     :submit
          :value    "登录"
          :on-click #(login! login-data errors)}]
        [:p.mt-5.mb-3.text-muted "&copy @2018"]]])))


(defn load-page []
  (reagent/render
    [login-component]
    (dom/by-id "app")))

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (load-page)))