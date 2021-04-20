(ns soul-talk.collect-link.db
  (:require [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]))

(defn save-collect-link
  [collect-link]
  (sql/insert! *db* :collect_link collect-link {:build-fn rs-set/as-unqualified-maps}))

(defn update-collect-link [collect-link]
  (sql/update! *db*
    :collect-links
    (select-keys collect-link [:title :url :image :description :update_by :update_at])
    (:id collect-link)))

(defn delete-collect-link
  [id]
  (sql/delete! *db* :collect_link ["id = ?" id]))

(defn gen-where [{:keys [title]}]
  (let [where-str " where title like ? "
        coll [(str "%" title "%")]]
    [where-str coll]))

(defn load-collect-links-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where params)
        sql-str (str "select * from collect_link " where " offset ? limit ? order by create_at desc")
        collect-links (sql/query *db*
                       (into [sql-str] (conj coll offset per_page))
                        {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from collect_link " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [collect-links total]))

(defn count-collect-links [{:keys [] :as params}]
  (let [sql-str (str "select count(1) from collect_link where 1 = 1 ")]
    (sql/query *db* [sql-str]
      {:builder-fn rs-set/as-unqualified-maps})))

(defn get-collect-link [id]
  (sql/get-by-id *db* :collect-links id))


;; tags
(defn save-collect-link-tag! [collect-link-tag]
  (sql/insert! *db* :collect-links_tags
    collect-link-tag
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-collect-link-tags! [collect-link-tags]
  (sql/insert-multi! *db*
    :collect-links-tags
    [:collect-link_id :tag_id]
    (map #([(:collect-link_id %) (:tag_id %)]) collect-link-tags
      {:builder-fn rs-set/as-unqualified-maps})))

(defn get-collect-link-tags-by-collect-link-id [collect-link-id]
  (sql/query *db*
    ["select * from collect_link_tag where collect_link_id = ?" collect-link-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-collect-link-tag-by-collect-link-id [collect-link-id]
  (sql/delete! *db* :collect-links_tags ["collect_link_id = ?" collect-link-id]))

(defn delete-collect-link-tag-by-id [id]
  (sql/delete! *db* :collect-links_tags ["id = ?" id]))

;; series

(defn save-collect-link-series! [collect-link-series]
  (sql/insert! *db* :collect-links_series
    collect-link-series
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-collect-links-series-all! [collect-links-series-all]
  (sql/insert-multi! *db*
    :collect-links-series
    [:collect-link_id :series_id]
    (map #([(:collect-link_id %) (:series_id %)]) collect-links-series-all
      {:builder-fn rs-set/as-unqualified-maps})))


(defn get-collect-link-series-by-collect-link-id [collect-link-id]
  (sql/query *db*
    ["select * from collect_link_series where collect_link_id = ?" collect-link-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-collect-link-series-by-collect-link-id [collect-link-id]
  (sql/delete! *db* :collect-links_series ["collect_link_id = ?" collect-link-id]))

(defn delete-collect-link-series-by-id [id]
  (sql/delete! *db* :collect-links_series ["id = ?" id]))

;; 评论
(defn save-collect-link-comment! [comment]
  (sql/insert! *db* :collect-links_comments comment {:builder-fn rs-set/as-unqualified-maps}))


(defn delete-collect-link-comment-by-id! [id]
  (sql/delete! *db* :collect-links_comments ["id = ? " id]))

(defn delete-collect-link-comments-by-collect-link-id! [collect-link-id]
  (sql/delete! *db* :collect-links_comments ["collect_link_id = ?" collect-link-id]))


(defn get-comments-by-collect-link-id [collect-link-id]
  (sql/query *db*
    ["select * from comment where collect_link_id = ? order by create_at desc" collect-link-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
    ["select 8 from comment where reply_id = ? order by create_at desc"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where-comments [{:keys [collect-links_id body create_by_name create_by_email]}]
  (let [[where-str coll] [(str " where 1 = 1") []]
        [where-str coll] (if (nil? collect-links_id)
                           [where-str coll]
                           [(str where-str " and collect_link_id = ?") (conj coll collect-links_id)])
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

(defn load-collect-links-comments-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where-comments params)
        query-str (str "select * from collect_link_comment " where " offset ? limit ?")
        comments (sql/query *db*
                   (into [query-str] (conj coll offset per_page))
                   {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from collect_link_comment " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [comments total]))
