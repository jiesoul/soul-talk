(ns soul-talk.collect-site.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-collect-site
  [collect-site]
  (sql/insert! *db* :collect_sites collect-site {:build-fn rs-set/as-unqualified-maps}))

(defn update-collect-site [collect-site]
  (sql/update! *db*
    :collect-sites
    (select-keys collect-site [:title :url :image :description])
    (:id collect-site)))

(defn get-collect-site [id]
  (sql/get-by-id *db* :collect_sites id))

(defn delete-collect-site
  [id]
  (sql/delete! *db* :collect_sites ["id = ?" id]))

(defn gen-where [{:keys [title]}]
  (let [where-str (str "where title like ?")
        coll [(str "%" title "%")]]
    [where-str coll]))

(defn load-collect-sites-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where params)
        sql-str (str "select * from collect_sites " where " offset ? limit ? ")
        collect-sites (sql/query *db*
                        (into [sql-str] (conj coll offset per_page))
                        {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from collect_sites " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [collect-sites total]))
