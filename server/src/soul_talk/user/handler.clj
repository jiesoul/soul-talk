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
      (-> {:result :ok
           :message "用户已登出"}
        (resp/ok)
        (assoc :session nil))))


;; 用户操作
(defn load-users []
  (let [users (user-db/select-all-users)]
    (resp/ok {:result :ok
              :users (->> users
                         (map #(assoc % :password nil)))})))

(defn update-password! [id {:keys [oldPassword newPassword] :as params}]
  (let [user (user-db/find-by-id (long id))]
    (if-not (hashers/check oldPassword (:password user))
      (resp/unauthorized {:result  :error
                          :message "旧密码错误"})
      (do
        (-> user
          (assoc :password (hashers/encrypt newPassword))
          (user-db/update-pass!))
        (resp/ok {:result :ok
                  :message "密码修改成功"})))))

(defn get-user-profile [id]
  (if-let [user (user-db/find-by-id id)]
    (-> {:result :ok
         :user   (assoc user :password nil)}
      (resp/ok))
    (resp/ok {:result :error
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







