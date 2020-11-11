(ns soul-talk.collect-link.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-collect-link
  [collect-link]
  (sql/insert! *db* :collect_link collect-link {:build-fn rs-set/as-unqualified-maps}))

(defn auth-collect-link
  [token]
  (let [sql-str (str "SELECT * FROM collect_links " " WHERE token = ?")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
      first)))

(defn delete-collect-link
  [id]
  (sql/delete! *db* :collect_link ["id = ?" id]))

(defn load-collect-links [{:keys [pre_page page offset]} {:keys [app_name] :as params}]
  (let [sql-str (str "select * from collect_link where 1=1 offset ? limit ? ")]
    (sql/query *db* [sql-str offset page]
      {:builder-fn rs-set/as-unqualified-maps})))

(defn count-collect-links [{:keys [] :as params}]
  (let [sql-str (str "select count(1) from collect_link where 1 = 1 ")]
    (sql/query *db* [sql-str]
      {:builder-fn rs-set/as-unqualified-maps})))
