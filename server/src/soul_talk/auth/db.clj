(ns soul-talk.auth.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]
            [soul-talk.database.db :refer [*db*]]
            [taoensso.timbre :as log]
            [next.jdbc :as jdbc]))

(defn save-token!
  [user-token]
  (first
    (sql/insert! *db* :auth_tokens user-token)))

(defn auth-token?
  [token]
  (let [sql-str (str "SELECT * FROM auth_tokens WHERE token = ? and refresh_at + interval '10 h' > now () "
                  " order by refresh_at desc")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
            first)))

(defn refresh-token! [{:keys [refresh_at token]}]
  (sql/update! *db* :auth_tokens {:refresh_at refresh_at} {:token token} {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-token-by-user-id!
  [user_id]
  (sql/delete! *db* :auth_tokens ["user_id = ?" user_id]))