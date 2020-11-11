(ns soul-talk.app-key.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-app-key
  [app-key]
  (sql/insert! *db* :app_keys app-key {:build-fn rs-set/as-unqualified-maps}))

(defn auth-app-key
  [token]
  (let [sql-str (str "SELECT * FROM app_keys " " WHERE token = ?")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
      first)))

(defn delete-app-key
  [id]
  (sql/delete! *db* :app_keys ["id = ?" id]))

(defn load-app-keys [{:keys [pre_page page offset]} {:keys [app_name] :as params}]
  (let [sql-str (str "select * from app_keys where 1=1 offset ? limit ? ")]
    (sql/query *db* [sql-str offset page]
      {:builder-fn rs-set/as-unqualified-maps})))

(defn count-app-keys [{:keys [] :as params}]
  (let [sql-str (str "select count(1) from app_keys where 1 = 1 ")]
    (sql/query *db* [sql-str]
      {:builder-fn rs-set/as-unqualified-maps})))
