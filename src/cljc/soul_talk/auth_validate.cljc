(ns soul-talk.auth-validate
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as log]))

(def ^:dynamic *password-re* #"^(?=.*\d).{4,128}$")

(def ^:dynamic *email-re* #"^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$")

(defn validate-email [email]
  (if (and (not (nil? email))
           (string? email)
           (re-matches *email-re* email))
    true
    false))

(defn validate-passoword [password]
  (if (and (not (nil? password))
           (string? password)
           (re-matches *password-re* password))
    true
    false))

(defn reg-errors [{:keys [pass-confirm] :as params}]
  (first
    (b/validate
      params
      :email [[v/required :message "email 不能为空"]
              [v/email :message "email 不合法"]]
      :password [[v/required :message "密码不能为空"]
                 [v/min-count 7 :message "密码最少8位"]
                 [= pass-confirm :message "两次密码必须一样"]])))

(defn login-errors [params]
  (first
    (b/validate
      params
      :email [[v/required :message "email 不能为空"]
              [v/email :message "email 不合法"]]
      :password [[v/required :message "密码不能为空"]
                 [v/min-count 7 :message "密码最少8位"]])))