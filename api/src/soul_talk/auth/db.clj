(ns soul-talk.auth.db
  (:require [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]))

(defn save-token!
  [user-token]
  (first
    (sql/insert! *db* :auth_token user-token)))

(defn auth-token?
  [token]
  (let [sql-str (str "SELECT * FROM auth_token WHERE token = ? and refresh_at + interval '10 h' > now () "
                  " order by refresh_at desc")
        tokens (sql/query *db* [sql-str token])]
    (some-> tokens
            first)))

(defn refresh-token! [{:keys [refresh_at token]}]
  (sql/update! *db* :auth_token {:refresh_at refresh_at} {:token token}))

(defn invalid-token-by-user-id!
  [user_id]
  (sql/delete! *db* :auth_token {:valid 0} ["user_id = ?" user_id]))