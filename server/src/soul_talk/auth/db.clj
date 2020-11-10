(ns soul-talk.auth.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]
            [soul-talk.database.db :refer [*db*]]
            [taoensso.timbre :as log]))

(defn save-token
  [user-token]
  (first
    (sql/insert! *db* :auth_tokens user-token)))

(defn auth-token
  [token]
  (let [sql-str (str "SELECT * FROM auth_tokens "
                  " WHERE token = ? and create_at + interval '10 h' > now()")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (log/info "验证token：" tokens)
    (some-> tokens
      first)))

(defn delete-token
  [token]
  (sql/delete! *db* :auth_tokens ["token = ?" token]))