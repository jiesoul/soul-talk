(ns soul-talk.user.db
  (:require [clojure.java.jdbc :as jdbc]
            [soul-talk.database.db :refer [*db*]]
            [crypto.random :refer [base64]]
            [taoensso.timbre :as log]))

(defn find-by [key value]
  (let [sql (str "select * from users where " key " = ?")]
    (first
      (jdbc/query *db* [sql value]))))

(defn find-by-id [id]
  (find-by "id" id))

(defn find-by-email [email]
  (find-by "email" email))

(defn insert-user! [user]
  (jdbc/insert! *db* :users user))

(defn select-all-users []
  (jdbc/query *db* ["SELECT * from users"]))

(defn update-login-time [{:keys [id last-time]}]
  (jdbc/update! *db* :users {:last_login last-time} ["id = ?" id]))


(defn update-pass! [{:keys [id password]}]
  (jdbc/update! *db* :users {:password password} ["id = ?" id]))

(defn save-user-profile! [{:keys [id name] :as user}]
  (jdbc/update! *db* :users user ["id = ?" id]))

(defn count-users []
  (:count
    (first
      (jdbc/query *db*
        ["SELECT count(email) as count from users"]))))

(defn gen-session-id
  []
  (base64 32))

(defn make-token
  [user-id]
  (let [token (gen-session-id)]
    (jdbc/insert! *db* :auth_tokens {:id token
                                    :user_id user-id})))

(defn authenticate-token
  [req token]
  (log/debug "auth request: " req)
  (let [sql-str (str "SELECT * FROM auth_tokens "
                  " WHERE id = ? and create_at + interval '10 h' > now()")
        tokens (jdbc/query *db* [sql-str token])]
    (some-> tokens
      first
      :user_id
      find-by-id)))