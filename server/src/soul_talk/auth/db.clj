(ns soul-talk.auth.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]
            [soul-talk.database.db :refer [*db*]]
            [crypto.random :refer [base64]]))

(defn gen-session-id
  []
  (base64 32))

(defn make-token
  [user-token]
  (first
    (sql/insert! *db* :auth_tokens user-token)))

(defn authenticate-token
  [req token]
  (let [sql-str (str "SELECT * FROM auth_tokens "
                  " WHERE id = ? and create_at + interval '10 h' > now()")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
      first
      :user_id)))

(defn disable-token
  [auth-token]
  (sql/delete! *db* :auth_tokens auth-token))