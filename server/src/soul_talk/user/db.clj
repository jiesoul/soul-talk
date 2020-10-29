(ns soul-talk.user.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]
            [soul-talk.database.db :refer [*db*]]
            [crypto.random :refer [base64]]
            [taoensso.timbre :as log]))

(defn find-by [query-map]
  (let [users (sql/find-by-keys *db* :users query-map {:builder-fn rs-set/as-unqualified-lower-maps})]
    (some-> users
      first)))

(defn find-by-id [id]
  (sql/get-by-id *db* :users id))

(defn find-by-email [email]
  (find-by {:email email}))

(defn insert-user! [user]
  (sql/insert! *db* :users user))

(defn select-all-users []
  (sql/query *db* ["SELECT * from users"]))

(defn update-login-time [{:keys [id last-time]}]
  (sql/update! *db* :users {:last_login_at last-time} ["id = ?" id]))


(defn update-pass! [{:keys [id password]}]
  (sql/update! *db* :users {:password password} ["id = ?" id]))

(defn save-user-profile! [{:keys [id name] :as user}]
  (sql/update! *db* :users user ["id = ?" id]))

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