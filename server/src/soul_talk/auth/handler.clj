(ns soul-talk.auth.handler
  (:require [soul-talk.utils :as utils]
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

;; token 验证
(def auth-backend (token-backend {:authfn       auth-db/authenticate-token
                                  :unauthorized utils/unauthorized}))

(def auth-api-key (token-backend {:authfn auth-db/authenticate-token
                                  :unauthorized utils/unauthorized}))

(defn admin?
  [request]
  (:headers request))

(defn login!
  [{:keys [session remote-addr] :as req} {:keys [email password]}]
  (if-let [{:keys [id ] :as user} (authenticate-local email password)]
    (do
      (-> user
        (assoc :last_login_at (l/local-date-time))
        (user-db/update-login-time))
      (let [token (auth-db/gen-session-id)
            user-token {:token token :user_id id}]
        (auth-db/make-token user-token)
        (log/info "user:" email " successfully logged from ip " remote-addr " Token: " token)
        (utils/ok "保存成功" {:user  (dissoc user :password)
                      :token token})))
    (utils/unauthorized "email或密码错误,登录失败")))

(defn logout! [{:keys [user_id token]}]
  (do
    (auth-db/disable-token {:user_id user_id :token token})
    (log/info "user_id: " user_id  " log out")
    (utils/ok "用户已登出")))
