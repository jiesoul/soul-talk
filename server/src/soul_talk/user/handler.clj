(ns soul-talk.user.handler
  (:require [soul-talk.user.db :as user-db]
            [ring.util.http-response :as resp]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [taoensso.timbre :as log]
            [java-time.local :as l]))

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

(defn login!
  [{:keys [session remote-addr] :as req} {:keys [email password]}]
  (if-let [{:keys [id ] :as user} (authenticate-local email password)]
    (do
      (-> user
        (assoc :last_login_at (l/local-date-time))
        (user-db/update-login-time))
      (let [token (user-db/gen-session-id)
            user-token {:id token :user_id id}]
        (user-db/make-token user-token)
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
    (user-db/disable-token auth-token)
    (log/info "user_id: " user_id  " log out")
    (-> {:result :ok
         :message "用户已登出"}
      (resp/ok)
      (assoc :session nil))))


;; 用户操作
(defn load-users []
  (let [users (user-db/select-all-users)]
    (resp/ok {:result :ok
              :data   {
                       :users (->> users
                                (map #(dissoc % :password)))}})))

(defn update-password! [id {:keys [oldPassword newPassword confirmPassword] :as params}]
  (let [user (user-db/find-by-id (long id))]
    (if (= newPassword confirmPassword)
      (if-not (= oldPassword newPassword)
        (if (hashers/check oldPassword (:password user))
          (let [user (-> user
                       (assoc :password (hashers/encrypt newPassword))
                       (user-db/update-pass!))]
            (resp/ok {:result  :ok
                      :message "密码修改成功"}))
          (resp/unauthorized {:result  :error
                              :message "旧密码错误"}))
        (resp/unauthorized {:result :error
                            :message "新密码不能和旧密码一致"}))
      (resp/unauthorized {:result :error
                          :message "新密码确认密码不一致"}))))

(defn get-user-profile [id]
  (if-let [user (user-db/find-by-id id)]
    (let [last_login_at (:last_login_at user)]
      (log/debug "load user: " user)
      (-> {:result :ok
           :user   (assoc user :password nil)}
        (resp/ok)))
    (resp/bad-request {:result :error
                       :message "未找到用户"})))

(defn save-user-profile! [id {:keys [username image] :as params}]
  (if-let [user (user-db/find-by-id id)]
    (let [user-profile (user-db/save-user-profile! (assoc user :name username))]
      (resp/ok {:result :ok
                :message "保存成功"}))
    (resp/bad-request {:result "error"
                       :message "未找到用户信息"})))

;; token 验证
(defn unauthorized-handler [req msg]
  {:status 401
   :body {:result :error
          :message (or msg "用户未验证")}})

(def auth-backend (token-backend {:authfn user-db/authenticate-token
                                  :unauthorized unauthorized-handler}))

(defn authenticated [req]
  (authenticated? req))

(defn admin [req]
  (authenticated? req))







