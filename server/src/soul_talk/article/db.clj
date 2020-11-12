(ns soul-talk.article.db
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]
            [soul-talk.tag.db :refer [add-tags-to-article]]
            [next.jdbc.result-set :as rs-set]))

(defn insert-article! [article]
  (sql/insert! *db* :articles article {:builder-fn rs-set/as-unqualified-maps}))

(defn save-article! [article]
  (let [tagList (:tagList article)
        id (:id article)
        article (-> article (dissoc :tagList))]
    (do
      (add-tags-to-article id tagList)
      (insert-article! article)
      )))

(defn update-article! [{:keys [id] :as article}]
  (sql/update! *db* :articles
    (select-keys article [:title :image :description :body :update_by :update_at])
    ["id = ?" id]))

(defn get-article-all []
  (sql/query *db* ["select * from articles order by create_at desc"] {:builder-fn rs-set/as-unqualified-maps}))

(defn gen-where [{:keys [title publish]}]
  (let [where-str (str "where title like ?")
        coll [(str "%" title "%")]
        where-str (if (nil? publish) where-str (str where-str " and publish = ?"))
        coll (if (nil? publish) coll (conj coll publish))]
    [where-str coll]))

(defn load-articles-page [{:keys [offset per-page]} params]
  (let [[where coll] (gen-where params)
        query-str (str "select * from articles " where " order by create_at desc offset ? limit ?")
        articles (sql/query *db*
                   (into [query-str] (conj coll offset per-page))
                   {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from articles " where )
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [articles total]))

(defn get-article-by-id [id]
  (sql/get-by-id *db* :articles id
    {:builder-fn rs-set/as-unqualified-maps}))

(defn publish-article! [id]
  (sql/update! *db* :articles {:publish 1} ["id = ?" id]))

(defn get-article-publish [id]
  (sql/query *db*
    ["select * from articles where publish = 1 and id = ?
              order by create_at desc " id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-article! [id]
  (sql/delete! *db* :articles ["id = ?" id]))

(defn get-article-archives []
  (sql/query *db*
    ["select year,month, count(month) as counter
                from
                  (select date_part('year', create_at) as year,
                    date_part('month', create_at) as month
                    from articles where publish = 1) t
                group by year,month"]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-article-archives-year-month [year month]
  (sql/query *db*
    ["select * from articles where publish = 1
      and date_part('year', create_at) = ?
      and date_part('month', create_at) = ?
      order by create_at desc"
     year month]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn save-article-tags! [article-tags]
  (sql/insert-multi! *db* :article-tags
    [:article_id :tag_id] (map #([(:article_id %) (:tag_id %)]) article-tags {:builder-fn rs-set/as-unqualified-maps})))

(defn delete-post-tag-by-post-id [article-id]
  (sql/delete! *db* :article-tags ["post_id = ?" article-id]))


(defn delete-post-tag-by-tag-id [tag-id]
  (sql/delete! *db* :article-tags ["tag_id = ?" tag-id] {:builder-fn rs-set/as-unqualified-maps}))

(defn save-comment! [comment]
  (sql/insert! *db* :comments comment {:builder-fn rs-set/as-unqualified-maps}))


(defn delete-comment! [id]
  (sql/delete! *db* :comments ["id = ? " id]))


(defn get-comments-by-articleId [article-id]
  (sql/query *db*
    ["select * from comments where article_id = ? order by create_at desc" article-id]
    {:builder-fn rs-set/as-unqualified-maps}))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
    ["select 8 from comments where reply_id = ? order by create_at desc"]
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
        query-str (str "select * from articles_comments " where " offset ? limit ?")
        comments (sql/query *db*
                   (into [query-str] (conj coll offset per_page))
                   {:builder-fn rs-set/as-unqualified-maps})
        count-str (str "select count(1) as c from articles_comments " where)
        total (:c
                (first
                  (sql/query *db*
                    (into [count-str] coll))))]
    [comments total]))


