(ns soul-talk.auth.handler
  (:require [soul-talk.utils :as utils]
            [soul-talk.auth.db :as auth-db]
            [soul-talk.user.db :as user-db]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [java-time.local :as l :refer [local-date-time]]
            [taoensso.timbre :as log]
            [soul-talk.auth.spec :as spec]))

(def login spec/login)
(def register spec/register)
(def logout spec/logout)

(defn register! [{:keys [session] :as req} user]
  (let [count (user-db/count-users)]
    (if (pos? count)
      (utils/bad-request "系统不允许注册！！")
      (if-let [temp-user (user-db/find-by-email (:email user))]
        (utils/unauthorized (str (:email temp-user) " 已被注册"))
        (do
          (user-db/insert-user!
            (-> user
              (dissoc :pass-confirm)
              (update :password hashers/derive)))
          (utils/ok))))))

(defn authenticate-local [email password]
  (when-let [user (user-db/find-by-email email)]
    (when (hashers/check password (:password user))
      user)))

(defn auth-token [token]
  (auth-db/auth-token? token))

(defn refresh-token [user-token]
  (auth-db/refresh-token! (assoc user-token :refresh_at (l/local-date-time))))

(defn login!
  [{:keys [session remote-addr] :as req} {:keys [email password]}]
  (if-let [{:keys [id ] :as user} (authenticate-local email password)]
    (do
      (-> user
        (assoc :last_login_at (l/local-date-time))
        (user-db/update-login-time!))
      (let [token (utils/gen-token)
            user-token {:token token :user_id id}]
        (auth-db/save-token! user-token)
        (log/info "user:" email " successfully logged from ip " remote-addr " Token: " token)
        (utils/ok "保存成功" {:user  (dissoc user :password)
                      :token token})))
    (utils/unauthorized "email或密码错误,登录失败")))

(defn logout! []
  (utils/ok "用户已登出"))
