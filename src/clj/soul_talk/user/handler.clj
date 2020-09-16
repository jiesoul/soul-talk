(ns soul-talk.user.handler
  (:require [soul-talk.user.db :as user-db]
            [clojure.spec.alpha :as s]
            [ring.util.http-response :as resp]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [taoensso.timbre :as log]
            [java-time.local :as l]))

(defn load-users []
  (let [users (user-db/select-all-users)]
    (resp/ok {:result :ok
              :users (->> users
                         (map #(assoc % :password nil)))})))

(defn update-password! [{:keys [id oldPassword newPassword] :as params}]
  (let [user (user-db/find-by-id id)]
    (if-not (hashers/check oldPassword (:password user))
      (resp/unauthorized {:result  :error
                          :message "旧密码错误"})
      (do
        (-> params
          (assoc :pass-new (hashers/encrypt newPassword))
          (user-db/change-pass!))
        (resp/ok {:result :ok})))))

(defn save-user-profile! [{:keys [id name image bio] :as params}]
  (let [user (user-db/find-by-id id)]
    (do
      (-> user
        (assoc :name name)
        (user-db/save-user-profile!))
      (-> {:result :ok
           :user   (assoc user :password nil)}
        (resp/ok)))))

(defn unauthorized-handler [req msg]
  {:status 401
   :body {:result :error
          :message (or msg "User not authorized")}})

(def auth-backend (token-backend {:authfn user-db/authenticate-token
                                  :unauthorized unauthorized-handler}))

(defn authenticated [req]
  (authenticated? req))

(defn admin [req]
  (authenticated? req))

(defn register! [{:keys [session] :as req} user]
  (let [count (user-db/count-users)]
    (if (pos? count)
      (resp/bad-request {:result  :error
                         :message "系统不允许注册！！"})
      (if-let [temp-user (user-db/find-by-email (:email user))]
        (resp/unauthorized {:result  :error
                            :message (str (:email temp-user) " 已被注册")})
        (do
          (user-db/insert-user!
            (-> user
              (dissoc :pass-confirm)
              (update :password hashers/encrypt)))
          (-> {:result :ok}
            (resp/ok)))))))

(defn authenticate-local [email password]
  (when-let [user (user-db/find-by-email email)]
    (when (hashers/check password (:password user))
      (dissoc user :password))))

(defn login!
  [{:keys [session remote-addr] :as req} {:keys [email password] :as user}]
  (if-let [user (authenticate-local email password)]
    (do
      (-> user
        (assoc :last-time (l/local-date-time))
        (user-db/update-login-time))
      (let [token (first (user-db/make-token (:id user)))]
        (log/info "user:" email " successfully logged from ip " remote-addr)
        (-> {:result :ok
             :user   user
             :token  (:id token)}
          (resp/ok))))
    (do
      (log/info "login failed for " email)
      (resp/unauthorized
        {:result  :error
         :message "email或密码错误,登录失败"}))))

(defn logout! []
  (do (log/info "user: " :session " log out")
      (-> {:result :ok}
        (resp/ok)
        (assoc :session nil))))
