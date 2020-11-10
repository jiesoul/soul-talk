(ns soul-talk.api-key.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-api-key
  [api-key]
  (sql/insert! *db* :app_keys api-key {:build-fn rs-set/as-unqualified-maps}))

(defn auth-api-key
  [token]
  (let [sql-str (str "SELECT * FROM app_keys " " WHERE token = ?)")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
      first)))

(defn delete-api-key
  [id]
  (sql/delete! *db* :api_keys ["id = ?" id]))

(defn load-all-api-keys []
  (sql/query *db* ["select * from app_keys"] {:build-fn rs-set/as-unqualified-maps}))
