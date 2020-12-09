(ns soul-talk.data-dic.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]
            [clojure.string :as str]))

(defn get-data-dic-all []
  (sql/query *db* ["select * from data_dic"]))

(defn save-data-dic [data-dic]
  (sql/insert! *db* :data_dic data-dic))

(defn update-data-dic [data-dic]
  (let [update-prop (select-keys data-dic [:name :pid :note :update_by :update_at])]
    (sql/update! *db* :data_dic
      update-prop
      (select-keys data-dic [:id]))))

(defn delete-data-dic-by-id [id]
  (sql/delete! *db* :data_dic ["id = ?" id]))

(defn delete-data-dic-by-pid [pid]
  (sql/delete! *db* :data_dic ["pid = ?" pid]))

(defn load-data-dices-by-pid [pid]
  (sql/query *db* ["select * from data_dic where pid = ?" pid] {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where [{:keys [name pid id]}]
  (let [[sql-str coll] [(str " where name like ? ") [(str "%" name "%")]]
        [sql-str coll] (if (str/blank? pid)
                         [sql-str coll]
                         [(str sql-str " and pid = ?") (conj coll pid)])
        [sql-str coll] (if (str/blank? id)
                         [sql-str coll]
                         [(str sql-str " and id = ?") (conj coll id)])]
    [sql-str coll]))

(defn load-data-dic-page [{:keys [offset per_page]} params]
  (let [where (gen-where params)
        sql-str (apply str "select * from data_dic" (first where) " offset ? limit ?")
        coll (conj (second where) offset per_page)]
    (sql/query *db*
      (into [sql-str] coll)
      {:builder-fn rs-set/as-unqualified-maps})))

(defn count-data-dic-page [params]
  (let [where (gen-where params)
        sql-str (str "select count(1) as c from data_dic" (first where))]
    (:c
     (first
       (sql/query *db* (into [sql-str] (second where)))))))

(defn get-data-dic-by-id [id]
  (sql/get-by-id *db* :data_dic id {:builder-fn rs-set/as-unqualified-maps}))

(defn get-data-dices-by-pid [pid]
  (sql/query *db*
    ["select * from data_dic where pid = ?" pid]
    {:builder-fn rs-set/as-unqualified-maps}))
