(ns soul-talk.user.handler
  (:require [soul-talk.user.db :as user-db]
            [soul-talk.utils :as utils]
            [buddy.hashers :as hashers]
            [soul-talk.user.spec :as spec]
            [soul-talk.pagination :as p]
            [cambium.core :as log]))

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
               :query-params params
               :pagination pagination})))

(defn load-users-auth-keys-page [req]
  (let [pagination (p/create req)
        params (:params req)
        [auth-keys total] (user-db/load-users-auth-keys-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:auth-keys auth-keys
                :query-params params 
                :pagination pagination}))) 
               

(defn update-password! [id {:keys [old-password new-password confirm-password] :as params}]
  (let [user (user-db/find-by-id (long id))]
    (if (= new-password confirm-password)
      (if-not (= old-password new-password)
        (if (hashers/check old-password (:password user))
          (let [user (-> user
                       (assoc :password (hashers/encrypt new-password))
                       (user-db/update-pass!))]
            (utils/ok "密码修改成功"))
          (utils/bad-request "旧密码错误"))
        (utils/bad-request "新密码不能和旧密码一致"))
      (utils/bad-request "新密码确认密码不一致"))))

(defn get-user-by-id [id]
  (if-let [user (user-db/find-by-id id)]
    (utils/ok {:user (assoc user :password nil)})
    (utils/bad-request "未找到用户")))

(defn update-user! [id {:keys [name image] :as params}]
  (if-let [user (user-db/find-by-id id)]
    (let [user-profile (user-db/save-user-profile! (assoc user :name name))]
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









