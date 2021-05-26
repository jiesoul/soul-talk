(ns soul-talk.article.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]
            [taoensso.timbre :as log]
            [clojure.string :as str]))

(defn insert-article! [article]
  (sql/insert! *db* :article article))

(defn add-article-tags! [article-id tags]
  (when (not-empty tags)
    (let [input (map vector (repeat article-id) tags)]
      (sql/insert-multi! *db* :article_tag
        [:article_id :tag_id]
        input))))

(defn delete-article-tags! [article-id]
  (sql/delete! *db* :article_tag ["article_id = ? " article-id]))

(defn save-article! [{:keys [id tags] :as article}]
  (add-article-tags! id tags)
  (sql/insert! *db* :article (dissoc article :tags)))

(defn update-article! [{:keys [id tags] :as article}]
  (delete-article-tags! id)
  (add-article-tags! id tags)
  (sql/update! *db* :article
    (select-keys article [:title :image :description :body :update_by :update_at :publish])
    ["id = ?" id]))

(defn get-article-all []
  (sql/query *db* ["select * from article order by create_at desc"] {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where [{:keys [title publish]}]
  (let [[where-str coll] [(str "where title like ?")
                          [(str "%" title "%")]]
        [where-str coll] (if (str/blank? publish)
                           [where-str coll]
                           [(str where-str " and publish = ?") (conj coll publish)])]
    [where-str coll]))

(defn load-articles-page [{:keys [offset per-page]} params]
  (let [[where coll] (gen-where params)
        query-str (str "select * from article " where " order by create_at desc offset ? limit ?")
        articles (sql/query *db*
                   (into [query-str] (conj coll offset per-page)))
        count-str (str "select count(1) as c from article " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [articles total]))

(defn get-article-by-id [id]
  (sql/get-by-id *db* :article id))

(defn get-article-tags-by-article-id [article-id]
  (sql/query *db* ["select * from article_tag where article_id = ? " article-id]))

(defn publish-article! [{:keys [id] :as article}]
  (sql/update! *db* :article
               (select-keys article [:publish :update_at :update_by])
               ["id = ?" id]))

(defn get-article-publish [id]
  (sql/query *db*
    ["select * from article where publish = 1 and id = ?
              order by create_at desc " id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-article! [id]
  (sql/delete! *db* :article ["id = ?" id]))

(defn get-article-archives []
  (sql/query *db*
    ["select year,month, count(month) as counter
                from
                  (select date_part('year', create_at) as year,
                    date_part('month', create_at) as month
                    from article where publish = '1101') t
                group by year,month"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-article-archives-year-month [year month]
  (sql/query *db*
    ["select * from article where publish = '1101'
      and date_part('year', create_at) = ?
      and date_part('month', create_at) = ?
      order by create_at desc"
     year month]
    {:builder-fn rs-set/as-unqualified-maps}))

;; tags
(defn save-article-tag! [article-tag]
  (sql/insert! *db* :article_tags
    article-tag
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-article-tags! [article-tags]
  (sql/insert-multi! *db*
    :article-tags
    [:article_id :tag_id]
    (map #([(:article_id %) (:tag_id %)]) article-tags)))

(defn get-article-tags-by-article-id [article-id]
  (sql/query *db*
    ["select * from article_tag where article_id = ?" article-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-article-tag-by-article-id [article-id]
  (sql/delete! *db* :article_tags ["article_id = ?" article-id]))

(defn delete-article-tag-by-id [id]
  (sql/delete! *db* :article_tags ["id = ?" id]))

;; category

(defn save-article-category! [article-category]
  (sql/insert! *db* :article_category
    article-category
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-article-category-all! [article-category-all]
  (sql/insert-multi! *db*
    :article-category
    [:article_id :category_id]
    (map #([(:article_id %) (:category_id %)]) article-category-all
      {:builder-fn rs-set/as-unqualified-maps})))


(defn get-article-category-by-article-id [article-id]
  (sql/query *db*
    ["select * from article_category where article_id = ?" article-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-article-category-by-article-id [article-id]
  (sql/delete! *db* :article_category ["article_id = ?" article-id]))

(defn delete-article-category-by-id [id]
  (sql/delete! *db* :article_category ["id = ?" id]))

;; 评论
(defn save-article-comment! [comment]
  (sql/insert! *db* :article_comments comment {:builder-fn rs-set/as-unqualified-maps}))


(defn delete-article-comment-by-id! [id]
  (sql/delete! *db* :article_comments ["id = ? " id]))

(defn delete-article-comments-by-article-id! [article-id]
  (sql/delete! *db* :article_comments ["article_id = ?" article-id]))


(defn get-comments-by-articleId [article-id]
  (sql/query *db*
    ["select * from comment where article_id = ? order by create_at desc" article-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
    ["select 8 from comment where reply_id = ? order by create_at desc"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where-comments [{:keys [article_id body create_by_name create_by_email]}]
  (let [[where-str coll] [(str " where 1 = 1") []]
        [where-str coll] (if (nil? article_id)
                           [where-str coll]
                           [(str where-str " and article_id = ?") (conj coll article_id)])
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

(defn load-articles-comments-page [{:keys [per_page offset]} params]
  (let [[where coll] (gen-where-comments params)
        query-str (str "select * from article_comment " where " offset ? limit ?")
        comments (sql/query *db*
                   (into [query-str] (conj coll offset per_page))
                   {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from article_comment " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [comments total]))










