(ns soul-talk.routes.user
  (:require [soul-talk.models.user-db :as user-db]
            [ring.util.http-response :as resp]
            [soul-talk.user-validate :refer [change-pass-errors]]
            [taoensso.timbre :as log]
            [buddy.hashers :as hashers]
            [soul-talk.routes.common :refer [handler]]
            [clojure.spec.alpha :as s]))

(def email-regex #"^[^@]+@[^@\\.]+[\\.].+")
(s/def ::email-type (s/and string? #(re-matches email-regex %)))
(s/def ::password string?)
(s/def ::pass-confirm string?)
(s/def ::email ::email-type)
(s/def ::pass-old string?)
(s/def ::pass-new string?)
(s/def ::name string?)

(def RegUser
  (s/def ::userReg (s/keys :req-un [::email ::password ::pass-confirm])))

(def LoginUser
  (s/def ::userLogin (s/keys :req-un [::email ::password])))

(def ChangePassUser
  (s/def ::userChangePass (s/keys :req-un [::email ::pass-old ::pass-new ::pass-confirm])))

(def User
  (s/def ::User (s/keys :req-un [::email]
                     :opt-un [::name])))

(handler load-users! []
  (let [users (user-db/select-all-users)]
    (resp/ok {:result :ok
              :users (->> users
                         (map #(assoc % :password nil)))})))

(handler change-pass! [{:keys [email pass-old pass-new] :as params}]
  (if-let [error (change-pass-errors params)]
    (resp/precondition-failed
      {:result :error
       :message error})
    (let [user (user-db/select-user email)]
      (if-not (hashers/check pass-old (:password user))
        (resp/unauthorized {:result  :error
                              :message "旧密码错误"})
        (do
          (-> params
              (assoc :pass-new (hashers/encrypt pass-new))
              (user-db/change-pass!))
          (resp/ok {:result :ok}))))))

(handler save-user-profile! [{:keys [email name] :as params}]
  (let [user (user-db/select-user email)]
    (do
      (-> user
        (assoc :name name)
        (user-db/save-user-profile!))
      (-> {:result :ok
           :user   (assoc user :password nil)}
        (resp/ok)))))