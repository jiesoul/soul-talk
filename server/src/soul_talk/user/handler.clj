(ns soul-talk.user.handler
  (:require [soul-talk.user.db :as user-db]
            [soul-talk.utils :as utils]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [java-time.local :as l]
            [soul-talk.user.spec :as spec]
            [soul-talk.pagination :as p]))

(def update-user spec/update-user)
(def user spec/user)
(def visible-user spec/visible-user)
(def update-password spec/update-password)
(def profile-user spec/profile-user)

;; 用户操作
(defn load-users []
  (let [users (user-db/select-all-users)]
    (utils/ok "保存成功" {:users (->> users (map #(dissoc % :password)))})))

(defn load-users-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [users total] (user-db/load-users-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:users users
               :pagination pagination})))

(defn update-password! [id {:keys [oldPassword newPassword confirmPassword] :as params}]
  (let [user (user-db/find-by-id (long id))]
    (if (= newPassword confirmPassword)
      (if-not (= oldPassword newPassword)
        (if (hashers/check oldPassword (:password user))
          (let [user (-> user
                       (assoc :password (hashers/encrypt newPassword))
                       (user-db/update-pass!))]
            (utils/ok "密码修改成功"))
          (utils/bad-request "旧密码错误"))
        (utils/bad-request "新密码不能和旧密码一致"))
      (utils/bad-request "新密码确认密码不一致"))))

(defn get-user-by-id [id]
  (if-let [user (user-db/find-by-id id)]
    (utils/ok {:user (assoc user :password nil)})
    (utils/bad-request "未找到用户")))

(defn update-user! [id {:keys [username image] :as params}]
  (if-let [user (user-db/find-by-id id)]
    (let [user-profile (user-db/save-user-profile! (assoc user :name username))]
      (utils/ok "保存成功"))
    (utils/bad-request "未找到用户信息")))

(defn delete-user! [id]
  (if (= id 1)
    (utils/bad-request "超级用户不允许删除")
    (let [rs (user-db/delete-user! id)]
      (utils/ok "删除成功"))))

(defn get-user-roles [id]
  (let [user-roles (user-db/get-user-roles id)]
    (utils/ok {:user-roles user-roles})))









