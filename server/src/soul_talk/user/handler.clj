(ns soul-talk.user.handler
  (:require [soul-talk.user.db :as user-db]
            [soul-talk.utils :as utils]
            [buddy.hashers :as hashers]
            [buddy.auth.accessrules :refer [success error restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.token :refer [token-backend]]
            [java-time.local :as l]))

;; 用户操作
(defn load-users []
  (let [users (user-db/select-all-users)]
    (utils/ok "保存成功" {:users (->> users (map #(dissoc % :password)))})))

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

(defn get-user-profile [id]
  (if-let [user (user-db/find-by-id id)]
    (utils/ok {:user (assoc user :password nil)})
    (utils/bad-request "未找到用户")))

(defn save-user-profile! [id {:keys [username image] :as params}]
  (if-let [user (user-db/find-by-id id)]
    (let [user-profile (user-db/save-user-profile! (assoc user :name username))]
      (utils/ok "保存成功"))
    (utils/bad-request "未找到用户信息")))









