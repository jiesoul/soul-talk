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

