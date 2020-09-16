(ns soul-talk.user.db
  (:require [clojure.java.jdbc :as sql]
            [soul-talk.database.db :refer [*db*]]
            [crypto.random :refer [base64]]
            [taoensso.timbre :as log]))

(defn find-by [key value]
  (let [sql (str "select * from users where " key " = ?")]
    (first (sql/query *db* [sql value]))))

(defn find-by-id [id]
  (find-by "id" id))

(defn find-by-email [email]
  (find-by "email" email))

(defn insert-user! [user]
  (sql/insert! *db* :users user))

(defn select-all-users []
  (sql/query *db* ["SELECT * from users"]))

(defn update-login-time [{:keys [id last-time]}]
  (sql/update! *db* :users {:last_login last-time} ["id = ?" id]))


(defn change-pass! [{:keys [id newPassword]}]
  (sql/update! *db* :users {:password newPassword} ["id = ?" id]))

(defn save-user-profile! [{:keys [id name]}]
  (sql/update! *db* :users {:name name} ["id = ?" id]))

(defn count-users []
  (:count
    (first
      (sql/query *db*
        ["SELECT count(email) as count from users"]))))

(defn gen-session-id
  []
  (base64 32))

(defn make-token
  [user-id]
  (let [token (gen-session-id)]
    (sql/insert! *db* :auth_tokens {:id token
                                    :user_id user-id})))

(defn authenticate-token
  [req token]
  (log/debug "auth request: " req)
  (let [sql-str (str "SELECT * FROM auth_tokens "
                  " WHERE id = ? and create_at + interval '10 h' > now()")
        tokens (sql/query *db* [sql-str token])]
    (some-> tokens
      first
      :user_id
      find-by-id)))