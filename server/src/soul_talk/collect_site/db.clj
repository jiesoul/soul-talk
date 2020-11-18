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


;; tags
(defn save-collect-site-tag! [collect-site-tag]
  (sql/insert! *db* :collect-sites_tags
    collect-site-tag
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-collect-sites-tags! [collect-sites-tags]
  (sql/insert-multi! *db*
    :collect-sites-tags
    [:collect_site_id :tag_id]
    (map #([(:collect_site_id %) (:tag_id %)]) collect-sites-tags
      {:builder-fn rs-set/as-unqualified-maps})))

(defn get-collect-sites-tags-by-collect-site-id [collect-site-id]
  (sql/query *db*
    ["select * from collect-sites_tags where collect_site_id = ?" collect-site-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-collect-site-tag-by-collect-site-id [collect-site-id]
  (sql/delete! *db* :collect-sites_tags ["collect-site_id = ?" collect-site-id]))

(defn delete-collect-site-tag-by-id [id]
  (sql/delete! *db* :collect-sites_tags ["id = ?" id]))

;; series

(defn save-collect-site-series! [collect-site-series]
  (sql/insert! *db* :collect-sites_series
    collect-site-series
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-collect-sites-series-all! [collect-sites-series-all]
  (sql/insert-multi! *db*
    :collect-sites-series
    [:collect-sites_id :series_id]
    (map #([(:collect-sites_id %) (:series_id %)]) collect-sites-series-all
      {:builder-fn rs-set/as-unqualified-maps})))


(defn get-collect-site-series-by-collect-site-id [collect-site-id]
  (sql/query *db*
    ["select * from collect-sites_series where collect-sites_id = ?" collect-sites-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-collect-site-series-by-collect-site-id [collect-site-id]
  (sql/delete! *db* :collect-sites_series ["collect-site_id = ?" collect-site-id]))

(defn delete-collect-site-series-by-id [id]
  (sql/delete! *db* :collect-sites_series ["id = ?" id]))

;; 评论
(defn save-collect-site-comment! [comment]
  (sql/insert! *db* :collect-sites_comments comment {:builder-fn rs-set/as-unqualified-maps}))


(defn delete-collect-site-comment-by-id! [id]
  (sql/delete! *db* :collect-sites_comments ["id = ? " id]))

(defn delete-collect-site-comments-by-collect-sites-id! [collect-sites-id]
  (sql/delete! *db* :collect-sites_comments ["collect_sites_id = ?" collect-sites-id]))


(defn get-comments-by-collect-site-id [collect-sites-id]
  (sql/query *db*
    ["select * from comments where collect_sites_id = ? order by create_at desc" collect-sites-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
    ["select 8 from comments where reply_id = ? order by create_at desc"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where-comments [{:keys [collect-sites_id body create_by_name create_by_email]}]
  (let [[where-str coll] [(str " where 1 = 1") []]
        [where-str coll] (if (nil? collect-sites_id)
                           [where-str coll]
                           [(str where-str " and collect_sites_id = ?") (conj coll collect-sites_id)])
        [where-str coll] (if (nil? body)
                           [where-str coll]
                           [(str where-str " and body like ?") (conj coll (str "%" body "%"))])
        [where-str coll] (if (nil? create_by_name)
                           [where-str coll]
                           [(str where-str " and create_by_name like ?") (conj coll (str "%" create_by_name "%"))])
        [where-str coll] (if (nil? create_by_email)
                           [where-str coll]
                           [(str where-str " and create_by_email like ?") (conj coll (str "%" create_by_email "%"))])]
    [where-str coll]))

(defn load-collect-sites-comments-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where-comments params)
        query-str (str "select * from collect-sites_comments " where " offset ? limit ?")
        comments (sql/query *db*
                   (into [query-str] (conj coll offset per_page))
                   {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from collect-sites_comments " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [comments total]))
