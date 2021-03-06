(ns soul-talk.routes.auth
  (:require [soul-talk.models.user-db :as user-db]
            [ring.util.http-response :as resp]
            [buddy.hashers :as hashers]
            [taoensso.timbre :as log]
            [soul-talk.auth-validate :refer [reg-errors login-errors]]
            [compojure.core :refer [defroutes POST GET routes]]
            [java-time.local :as l]
            [soul-talk.routes.common :refer [handler]]
            [soul-talk.models.auth-model :refer [make-token!]]
            [soul-talk.routes.user :refer [RegUser LoginUser]]))


(handler register! [{:keys [session] :as req} user]
  (if-let [error (reg-errors user)]
    (resp/precondition-failed
      {:result  :error
       :message error})
    (let [count (user-db/count-users)]
      (if (pos? count)
        (resp/bad-request {:result :error
                           :message "系统不允许注册！！"})
        (if-let [temp-user (user-db/select-user (:email user))]
          (resp/unauthorized {:result  :error
                                :message (str (:email temp-user) " 已被注册")})
          (do
            (user-db/save-user!
              (-> user
                (dissoc :pass-confirm)
                (update :password hashers/encrypt)))
            (-> {:result :ok}
              (resp/ok))))))))

(defn authenticate-local [email password]
  (when-let [user (user-db/select-user email)]
    (when (hashers/check password (:password user))
      (dissoc user :password))))

(handler login! [{:keys [session remote-addr] :as req} {:keys [email password] :as user}]
  (if-let [error (login-errors user)]
    (resp/precondition-failed
      {:result  :error
       :message (first error)})
    (if-let [user (authenticate-local email password)]
      (do
        (-> user
          (assoc :last-time (l/local-date-time))
          (user-db/update-login-time))
        (let [token (first (make-token! (:id user)))]
          (log/info "user:" email " successfully logged from ip " remote-addr)
          (-> {:result :ok
               :user   user
               :token  (:id token)}
            (resp/ok))))
      (do
        (log/info "login failed for " email)
        (resp/unauthorized
          {:result  :error
           :message "email或密码错误,登录失败"})))))

(handler logout! []
  (do (log/info "user: " :session " log out")
      (-> {:result :ok}
          (resp/ok)
          (assoc :session nil))))

