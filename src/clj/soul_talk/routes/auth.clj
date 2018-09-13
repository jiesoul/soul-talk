(ns soul-talk.routes.auth
  (:require [soul-talk.models.user-db :as user-db]
            [ring.util.http-response :as resp]
            [buddy.hashers :as hashers]
            [taoensso.timbre :as log]
            [soul-talk.auth-validate :refer [reg-errors login-errors change-pass-errors]]
            [compojure.core :refer [defroutes POST GET]]
            [java-time.local :as l]))

(defn register! [{:keys [session] :as req} user]
  (if-let [error (reg-errors user)]
    (resp/precondition-failed
      {:result  :error
       :message error})
    (try
      (if-let [temp-user (user-db/select-user (:email user))]
        (resp/internal-server-error {:result  :error
                                        :message (str (:email temp-user) " 已被注册")})
        (do
          (user-db/save-user!
            (-> user
                (dissoc :pass-confirm)
                (update :password hashers/encrypt)))
          (-> {:result :ok}
              (resp/ok))))
      (catch Exception e
        (do
          (log/error e)
          (resp/internal-server-error
            {:result :error
             :message "发生内部错误，请联系管理员"}))))))

(defn authenticate-local [email password]
  (when-let [user (user-db/select-user email)]
    (when (hashers/check password (:password user))
      (dissoc user :password))))

(defn login! [{:keys [session] :as req} {:keys [email password] :as user}]
  (if-let [error (login-errors user)]
    (resp/precondition-failed
      {:result :error
       :message error})
    (try
      (if-let [user (authenticate-local email password)]
        (do
          (-> user
              (assoc :last-time (l/local-date-time))
              (user-db/update-login-time))
          (log/info "user:" email " successfully logged in")
          (-> {:result :ok
               :user   user}
              (resp/ok)
              (assoc :session (assoc session :identity user))))
        (do
          (log/info "login failed for " email)
          (resp/unauthorized
           {:result  :error
            :message "email或密码错误,登录失败"})))
      (catch Exception e
        (do
          (log/error e)
          (resp/internal-server-error
            {:result :error
             :message "发生内部错误，请联系管理员"}))))))

(defn logout! []
  (-> {:result :ok}
      (resp/ok)
      (assoc :session nil)))

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