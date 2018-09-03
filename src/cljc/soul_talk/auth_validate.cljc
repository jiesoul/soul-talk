(ns soul-talk.auth-validate)

(def ^:dynamic *password-re* #"^(?=.*\d).{4,128}$")

(def ^:dynamic *email-re* #"^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$")

(defn validate-email [email]
  (if (re-matches *email-re* email)
    true
    false))

(defn validate-passoword [password]
  (if (re-matches *password-re* password)
    true
    false))