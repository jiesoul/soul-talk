(ns soul-talk.data-dic.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn get-data-dic-all []
  (sql/query *db* ["select * from data_dics"]))

(defn save-data-dic [data-dic]
  (sql/insert! *db* :data_dics data-dic))

(defn update-data-dic [data-dic]
  (let [update-prop (select-keys data-dic [:name :pid :note :update_by :update_at])]
    (sql/update! *db* :data_dics
      update-prop
      (select-keys data-dic [:id]))))

(defn delete-data-dic-by-id [id]
  (sql/delete! *db* :data_dics ["id = ?" id]))

(defn delete-data-dic-by-pid [pid]
  (sql/delete! *db* :data_dics ["pid = ?" pid]))

(defn load-data-dics-by-pid [pid]
  (sql/query *db* ["select * from data_dics where pid = ?" pid]))

(defn gen-where [{:keys [name pid]}]
  (let [sql-str " where name like ? "
        coll    [(str "%" name "%")]
        sql-str (if (nil? pid) sql-str (str sql-str " and pid = ?"))
        coll (if (nil? pid) coll (conj coll pid) )]
    (vector sql-str coll)))

(defn load-data-dic-page [{:keys [offset per_page]} params]
  (let [where (gen-where params)
        sql-str (apply str "select * from data_dics" (first where) " offset ? limit ?")
        coll (conj (second where) offset per_page)]
    (sql/query *db*
      (into [sql-str] coll))))

(defn count-data-dic-page [params]
  (let [where (gen-where params)
        sql-str (str "select count(1) as c from data_dics" (first where))]
    (:c
     (first
       (sql/query *db* (into [sql-str] (second where)))))))

(defn get-data-dic-by-id [id]
  (sql/get-by-id *db* :data_dics id))
