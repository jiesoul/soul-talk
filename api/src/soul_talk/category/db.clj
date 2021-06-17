(ns soul-talk.category.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs-set]))

(defn gen-where [{:keys [name]}]
  (let [where-str (str "where name like ?")
        coll [(str "%" name "%")]]
    [where-str coll]))

(defn load-category-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where params)
        sql-str (str "select * from category " where " offset ? limit ?")
        category (sql/query *db*
                  (into [sql-str] (conj coll offset per_page))
                  {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from category " where)
        total (:c
                (first
                  (sql/query *db* (into [count-str] coll))))]
    [category total]))

(defn save-category [category]
  (sql/insert! *db* :category category))

(defn update-category [{:keys [id] :as category}]
  (sql/update! *db* :category category {:id id}))

(defn delete-category [id]
  (sql/delete! *db* :category ["id = ?" id]))

(defn get-category-by-id [id]
  (sql/get-by-id *db* :category id))
