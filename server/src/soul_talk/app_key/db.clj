(ns soul-talk.app-key.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-app-key!
  [app-key]
  (sql/insert! *db* :app_key app-key {:builder-fn rs-set/as-unqualified-maps}))

(defn auth-app-key
  [token]
  (let [sql-str (str "SELECT * FROM app_key " " WHERE token = ?")
        tokens (sql/query *db* [sql-str token] {:builder-fn rs-set/as-unqualified-maps})]
    (some-> tokens
      first)))

(defn get-app-key [id]
  (sql/get-by-id *db* :app_key id))

(defn update-app-key! [{:keys [id] :as app-key}]
  (sql/update! *db* :app_key 
               (select-keys app-key [:token :valid])
               ["id = ? " id]))

(defn delete-app-key!
  [id]
  (sql/delete! *db* :app_key ["id = ?" id]))

(defn gen-where [{:keys [name pid]}]
  (let [sql-str " where app_name like ? "
        coll    [(str "%" name "%")]
        sql-str (if (nil? pid) sql-str (str sql-str " and pid = ?"))
        coll (if (nil? pid) coll (conj coll pid) )]
    (vector sql-str coll)))

(defn load-app-keys-page [{:keys [per_page offset]} params]
  (let [[where coll]   (gen-where params)
        sql-str (str "select * from app_key " where " offset ? limit ?")
        app-keys (sql/query *db*
                  (into [sql-str] (conj coll offset per_page))
                  {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from app_key " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [app-keys total]))

(defn get-app-key-by-name [app_name]
  (sql/find-by-keys *db* :app_key {:app_name app_name}))

(defn update-app-key [{:keys [token app_name]}]
  (sql/update! *db* :app_key {:token token} {:app_name app_name}))
