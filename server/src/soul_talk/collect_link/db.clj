(ns soul-talk.collect-link.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-collect-link
  [collect-link]
  (sql/insert! *db* :collect_links collect-link {:build-fn rs-set/as-unqualified-maps}))

(defn update-collect-link [collect-link]
  (sql/update! *db*
    :collect-links
    (select-keys collect-link [:title :url :image :description :update_by :update_at])
    (:id collect-link)))

(defn delete-collect-link
  [id]
  (sql/delete! *db* :collect_links ["id = ?" id]))

(defn gen-where [{:keys [title]}]
  (let [where-str " where title like ? "
        coll [(str "%" title "%")]]
    [where-str coll]))

(defn load-collect-links-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where params)
        sql-str (str "select * from collect_links " where " offset ? limit ? order by create_at desc")
        collect-links (sql/query *db*
                       (into [sql-str] (conj coll offset per_page))
                        {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from collect_links " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [collect-links total]))

(defn count-collect-links [{:keys [] :as params}]
  (let [sql-str (str "select count(1) from collect_links where 1 = 1 ")]
    (sql/query *db* [sql-str]
      {:builder-fn rs-set/as-unqualified-maps})))

(defn get-collect-link [id]
  (sql/get-by-id *db* :collect-links id))
