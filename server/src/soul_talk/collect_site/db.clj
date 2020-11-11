(ns soul-talk.collect-site.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-collect-site
  [collect-site]
  (sql/insert! *db* :collect_sites collect-site {:build-fn rs-set/as-unqualified-maps}))

(defn auth-collect-site
  [token]
  (let [sql-str (str "SELECT * FROM collect_sites " " WHERE token = ?")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
      first)))

(defn delete-collect-site
  [id]
  (sql/delete! *db* :collect_sites ["id = ?" id]))

(defn load-collect-sites [{:keys [pre_page page offset]} {:keys [app_name] :as params}]
  (let [sql-str (str "select * from collect_sites where 1=1 offset ? limit ? ")]
    (sql/query *db* [sql-str offset page]
      {:builder-fn rs-set/as-unqualified-maps})))

(defn count-collect-sites [{:keys [] :as params}]
  (let [sql-str (str "select count(1) from collect_sites where 1 = 1 ")]
    (sql/query *db* [sql-str]
      {:builder-fn rs-set/as-unqualified-maps})))
