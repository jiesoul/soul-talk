(ns soul-talk.app-key.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]
            [cambium.core :as log]))

(defn save-app-key!
  [app-key]
  (sql/insert! *db* :app_key app-key))

(defn auth-app-key
  [token]
  (let [tokens (sql/query *db* ["select * from app_key where token = ?" token])]
    (first tokens)))

(defn auth-token?
  [token]
  (let [sql-str (str "SELECT * FROM app_key WHERE token = ? ")
        tokens (sql/query *db* [sql-str token])]
    (some-> tokens
      first)))

(defn refresh-token! [{:keys [refresh_at token]}]
  (sql/update! *db* :auth_token {:refresh_at refresh_at} {:token token}))

(defn get-app-key [id]
  (sql/get-by-id *db* :app_key id))

(defn update-app-key! [{:keys [id] :as app-key}]
  (sql/update! *db* :app_key 
               (select-keys app-key [:token :is_valid :refresh_at])
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
                  (into [sql-str] (conj coll offset per_page)))
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
