(ns soul-talk.reply.db
  (:require [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [taoensso.timbre :as log]
            [clojure.string :as str]))

(defn insert-reply! [reply]
  (sql/insert! *db* :reply reply))

(defn add-reply-tags! [reply-id tags]
  (when (not-empty tags)
    (let [input (map vector (repeat reply-id) tags)]
      (sql/insert-multi! *db* :reply_tag
        [:reply_id :tag_id]
        input))))

(defn delete-reply-tags! [reply-id]
  (sql/delete! *db* :reply_tag ["reply_id = ? " reply-id]))

(defn save-reply! [{:keys [id tags] :as reply}]
  (add-reply-tags! id tags)
  (sql/insert! *db* :reply (dissoc reply :tags)))

(defn update-reply! [{:keys [id tags] :as reply}]
  (delete-reply-tags! id)
  (add-reply-tags! id tags)
  (sql/update! *db* :reply
    (select-keys reply [:title :image :description :body :update_by :update_at :publish])
    ["id = ?" id]))

(defn get-reply-all []
  (sql/query *db* ["select * from reply order by create_at desc"] {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where [{:keys [title publish]}]
  (let [[where-str coll] [(str "where title like ?")
                          [(str "%" title "%")]]
        [where-str coll] (if (str/blank? publish)
                           [where-str coll]
                           [(str where-str " and publish = ?") (conj coll publish)])]
    [where-str coll]))

(defn load-replies-page [{:keys [offset per-page]} params]
  (let [[where coll] (gen-where params)
        query-str (str "select * from reply " where " order by create_at desc offset ? limit ?")
        replies (sql/query *db*
                   (into [query-str] (conj coll offset per-page)))
        count-str (str "select count(1) as c from reply " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [replies total]))

(defn get-reply-by-id [id]
  (sql/get-by-id *db* :reply id))

(defn get-reply-tags-by-reply-id [reply-id]
  (sql/query *db* ["select * from reply_tag where reply_id = ? " reply-id]))

(defn publish-reply! [{:keys [id] :as reply}]
  (sql/update! *db* :reply
               (select-keys reply [:publish :update_at :update_by])
               ["id = ?" id]))

(defn get-reply-publish [id]
  (sql/query *db*
    ["select * from reply where publish = 1 and id = ?
              order by create_at desc " id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-reply! [id]
  (sql/delete! *db* :reply ["id = ?" id]))

(defn get-reply-archives []
  (sql/query *db*
    ["select year,month, count(month) as counter
                from
                  (select date_part('year', create_at) as year,
                    date_part('month', create_at) as month
                    from reply where publish = '1101') t
                group by year,month"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-reply-archives-year-month [year month]
  (sql/query *db*
    ["select * from reply where publish = '1101'
      and date_part('year', create_at) = ?
      and date_part('month', create_at) = ?
      order by create_at desc"
     year month]
    {:builder-fn rs-set/as-unqualified-maps}))

;; tags
(defn save-reply-tag! [reply-tag]
  (sql/insert! *db* :reply_tags
    reply-tag
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-reply-tags! [reply-tags]
  (sql/insert-multi! *db*
    :reply-tags
    [:reply_id :tag_id]
    (map #([(:reply_id %) (:tag_id %)]) reply-tags)))

(defn get-reply-tags-by-reply-id [reply-id]
  (sql/query *db*
    ["select * from reply_tag where reply_id = ?" reply-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-reply-tag-by-reply-id [reply-id]
  (sql/delete! *db* :reply_tags ["reply_id = ?" reply-id]))

(defn delete-reply-tag-by-id [id]
  (sql/delete! *db* :reply_tags ["id = ?" id]))

;; category

(defn save-reply-category! [reply-category]
  (sql/insert! *db* :reply_category
    reply-category
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-reply-category-all! [reply-category-all]
  (sql/insert-multi! *db*
    :reply-category
    [:reply_id :category_id]
    (map #([(:reply_id %) (:category_id %)]) reply-category-all
      {:builder-fn rs-set/as-unqualified-maps})))


(defn get-reply-category-by-reply-id [reply-id]
  (sql/query *db*
    ["select * from reply_category where reply_id = ?" reply-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-reply-category-by-reply-id [reply-id]
  (sql/delete! *db* :reply_category ["reply_id = ?" reply-id]))

(defn delete-reply-category-by-id [id]
  (sql/delete! *db* :reply_category ["id = ?" id]))

;; 评论
(defn save-reply-comment! [comment]
  (sql/insert! *db* :reply_comments comment {:builder-fn rs-set/as-unqualified-maps}))


(defn delete-reply-comment-by-id! [id]
  (sql/delete! *db* :reply_comments ["id = ? " id]))

(defn delete-reply-comments-by-reply-id! [reply-id]
  (sql/delete! *db* :reply_comments ["reply_id = ?" reply-id]))


(defn get-comments-by-replyId [reply-id]
  (sql/query *db*
    ["select * from comment where reply_id = ? order by create_at desc" reply-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
    ["select 8 from comment where reply_id = ? order by create_at desc"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where-comments [{:keys [reply_id body create_by_name create_by_email]}]
  (let [[where-str coll] [(str " where 1 = 1") []]
        [where-str coll] (if (nil? reply_id)
                           [where-str coll]
                           [(str where-str " and reply_id = ?") (conj coll reply_id)])
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

(defn load-replies-comments-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where-comments params)
        query-str (str "select * from reply_comment " where " offset ? limit ?")
        comments (sql/query *db*
                   (into [query-str] (conj coll offset per_page))
                   {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from reply_comment " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [comments total]))










