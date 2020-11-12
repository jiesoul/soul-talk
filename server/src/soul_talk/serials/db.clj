(ns soul-talk.serials.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]))

(defn gen-where [{:keys [name]}]
  (let [where-str (str "where name like ?")
        coll [(str "%" (.trim name) "%")]]
    [where-str coll]))

(defn load-serials-page [{:keys [per_page offset]} params]
  (let [where (gen-where params)
        sql-str (str "select * from serials " (first where) " offset ? limit")]
    (sql/query *db*
      (into [sql-str] (second where))
      {:builder-fn rs-set/as-unqualified-maps})))

(defn count-serials-page [params]
  (let [where (gen-where params)
        sql-str (str "select count(1) as c from serials " (first where))]
    (:c
      (first
        (sql/query *db* (into [sql-str] (second where)))))))

(defn save-serials [serials]
  (sql/insert! *db* :serials serials {:builder-fn rs-set/as-unqualified-maps}))

(defn update-serials [{:keys [id] :as serials}]
  (sql/update! *db* :serials serials {:id id}))

(defn delete-serials [id]
  (sql/delete! *db* :serials [:id id]))
