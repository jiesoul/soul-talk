(ns soul-talk.auth-validate
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as log]))


(defn reg-errors [{:keys [password] :as params}]
  (first
    (b/validate
      params
      :email [[v/required :message "email 不能为空"]
              [v/email :message "email 不合法"]]
      :password [[v/required :message "密码不能为空"]
                 [v/min-count 7 :message "密码最少8位"]]
      :pass-confirm [[= password :message "两次密码必须一样"]])))

(defn login-errors [params]
  (first
    (b/validate
      params
      :email [[v/required :message "email 不能为空"]
              [v/email :message "email 不合法"]]
      :password [[v/required :message "密码不能为空"]
                 [v/min-count 7 :message "密码最少8位"]])))

(defn change-pass-errors [{:keys [pass-old pass-new] :as params}]
  (first
    (b/validate
      params
      :pass-old [[v/required :message "旧密码不能为空"]
                 [v/min-count 7 :message "旧密码至少8位"]]
      :pass-new [[v/required :message "新密码不能为空"]
                 [v/min-count 7 :message "新密码至少8 位"]
                 [not= pass-old :message "新密码不能和旧密码一样"]]
      :pass-confirm [[= pass-new :message "确认密码必须和新密码相同"]])))