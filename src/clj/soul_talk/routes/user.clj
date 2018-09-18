(ns soul-talk.routes.user
  (:require [soul-talk.models.user-db :as user-db]
            [ring.util.http-response :as resp]
            [soul-talk.auth-validate :refer [change-pass-errors]]
            [taoensso.timbre :as log]
            [buddy.hashers :as hashers]))

(defn load-users! []
  (let [users (user-db/select-all-users)]
    (resp/ok {:result :ok
              :users (->> users
                         (map #(assoc % :password nil)))})))

(defn change-pass! [{:keys [email pass-old pass-new] :as params}]
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

(defn save-user-profile! [{:keys [email name] :as params}]
  (try
    (let [user (user-db/select-user email)]
      (do
        (-> user
            (assoc :name name)
            (user-db/save-user-profile!))
        (-> {:result :ok
             :user (assoc user :password nil)}
            (resp/ok))))
    (catch Throwable t
      (resp/internal-server-error
        {:result :error
         :message "发生未知错误"}))))