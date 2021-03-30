(ns soul-talk.series.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]))

(defn gen-where [{:keys [name]}]
  (let [where-str (str "where name like ?")
        coll [(str "%" name "%")]]
    [where-str coll]))

(defn load-series-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where params)
        sql-str (str "select * from series " where " offset ? limit ?")
        series (sql/query *db*
                  (into [sql-str] (conj coll offset per_page))
                  {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from series " where)
        total (:c
                (first
                  (sql/query *db* (into [count-str] coll))))]
    [series total]))

(defn save-series [series]
  (sql/insert! *db* :series series))

(defn update-series [{:keys [id] :as series}]
  (sql/update! *db* :series series {:id id}))

(defn delete-series [id]
  (sql/delete! *db* :series ["id = ?" id]))

(defn get-series-by-id [id]
  (sql/get-by-id *db* :series id {:builder-fn rs-set/as-unqualified-maps}))
