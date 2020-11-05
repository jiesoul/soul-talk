(ns soul-talk.auth.handler
  (:require [soul-talk.utils :refer [parse-int]]
            [ring.util.http-response :as resp]
            [soul-talk.auth.db :as auth-db]
            [soul-talk.user.db :as user-db]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [java-time.local :as l]
            [taoensso.timbre :as log]))



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
              (update :password hashers/derive)))
          (-> {:result :ok}
            (resp/ok)))))))

(defn authenticate-local [email password]
  (when-let [user (user-db/find-by-email email)]
    (when (hashers/check password (:password user))
      user)))

;; token 验证
(defn unauthorized-handler [req msg]
  {:status 401
   :body {:result :error
          :message (or msg "用户未验证")}})

(def auth-backend (token-backend {:authfn auth-db/authenticate-token
                                  :unauthorized unauthorized-handler}))

(defn authenticated [req]
  (authenticated? req))

(defn admin [req]
  (authenticated? req))

(defn login!
  [{:keys [session remote-addr] :as req} {:keys [email password]}]
  (if-let [{:keys [id ] :as user} (authenticate-local email password)]
    (do
      (-> user
        (assoc :last_login_at (l/local-date-time))
        (user-db/update-login-time))
      (let [token (auth-db/gen-session-id)
            user-token {:id token :user_id id}]
        (auth-db/make-token user-token)
        (log/info "user:" email " successfully logged from ip " remote-addr " Token: " token)
        (-> {:result :ok
             :data   {:user  (dissoc user :password)
                      :token token}}
          (resp/ok))))
    (do
      (log/info "login failed for " email)
      (resp/unauthorized
        {:result  :error
         :message "email或密码错误,登录失败"}))))

(defn logout! [{:keys [user_id] :as auth-token}]
  (do
    (auth-db/disable-token auth-token)
    (log/info "user_id: " user_id  " log out")
    (-> {:result :ok
         :message "用户已登出"}
      (resp/ok)
      (assoc :session nil))))
