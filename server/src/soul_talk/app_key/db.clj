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

(defn gen-where [{:keys [name pid]}]
  (let [sql-str " where name like ? "
        coll    [(str "%" name "%")]
        sql-str (if (nil? pid) sql-str (str sql-str " and pid = ?"))
        coll (if (nil? pid) coll (conj coll pid) )]
    (vector sql-str coll)))

(defn load-app-keys [{:keys [per_page offset]} params]
  (let [where (gen-where params)
        sql-str (str "select * from app_keys " (first where) " offset ? limit ?")]
    (sql/query *db* (into [sql-str] (second where))
      {:builder-fn rs-set/as-unqualified-maps})))

(defn count-app-keys [params]
  (let [where (gen-where params)
        sql-str (str "select count(1) as c from app_keys " (first where))]
    (first (sql/query *db* (into [sql-str] (second where))
             {:builder-fn rs-set/as-unqualified-maps}))))

(defn load-app-keys-page [{:keys [page offset]} page] {:keys [app_name] :as params}
  (let []))
