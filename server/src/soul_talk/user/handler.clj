(ns soul-talk.user.handler
  (:require [soul-talk.user.db :as user-db]
            [ring.util.http-response :as resp]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [taoensso.timbre :as log]
            [java-time.local :as l]))




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









