(ns soul-talk.article.db
  (:require [clojure.java.jdbc :as jdbc]
            [soul-talk.database.db :refer [*db*]]))

(defn tags-with-names [tag-names]
  (jdbc/query *db*
    ["select * from tag where in ? " tag-names]))

(defn add-tags-to-article [article-id tag-names]
  (when-not (empty? tag-names)
    (let [tags (tags-with-names tag-names)
          inputs (mapv #(hash-map :articleId article-id :tagId (:id %)) tags)]
      (jdbc/insert-multi! *db* :articleTags inputs))))

(defn save-article-tags! [article-tags]
  (jdbc/insert-multi! *db*
    :article-tags
    article-tags))

(defn delete-post-tag-by-post-id [article-id]
  (jdbc/delete! *db* :article-tags ["post_id = ?" article-id]))


(defn delete-post-tag-by-tag-id [tag-id]
  (jdbc/delete! *db* :article-tags ["tag_id = ?" tag-id]))

(defn insert-article! [article]
  (jdbc/insert! *db* :article article))

(defn save-article! [article]
  (let [tagList (:tagList article)
        id (:id article)
        article (-> article (dissoc :tagList))]
    (do
      (insert-article! article)
      ;(when-not (empty? tagList)
      ;  (add-tags-to-article id tagList))
      )))

(defn update-article! [{:keys [id] :as article}]
  (jdbc/update! *db* :article article ["id = ?" id]))

(defn get-article-all []
  (jdbc/query *db* ["select * from article order by createAt desc"]))

(defn get-article-page [{:keys [offset pre-page]}]
  (jdbc/query *db*
    ["select * from article order by createAt desc
                offset ? limit ?" offset pre-page]))

(defn count-article-all []
  (:count
    (first
      (jdbc/query *db*
        ["select count(id) as count from article"]))))

(defn get-article-by-id [id]
  (jdbc/query *db* ["SELECT * FROM article where id = ? " id]
    {:result-set-fn first}))

(defn publish-article! [id]
  (jdbc/update! *db* :article {:publish 1} ["id = ?" id]))

(defn get-article-publish []
  (jdbc/query *db*
    ["select * from article where publish = 1
              order by createAt desc "]))

(defn get-article-publish-page [{:keys [offset pre-page]}]
  (jdbc/query *db*
    ["select * from article where publish = 1
              order by createAt desc offset ? limit ? "
     offset pre-page]))

(defn count-article-publish []
  (:count
    (first
      (jdbc/query *db*
        ["select count(id) as count from article where publish = 1"]))))

(defn delete-article! [id]
  (jdbc/delete! *db* :article ["id = ?" id]))

(defn get-article-archives []
  (jdbc/query *db*
    ["select year,month, count(month) as counter
                from
                  (select date_part('year', createAt) as year,
                    date_part('month', createAt) as month
                    from article where publish = 1) t
                group by year,month"]))

(defn get-article-archives-year-month [year month]
  (jdbc/query *db*
    ["select * from article where publish = 1
      and date_part('year', createAt) = ?
      and date_part('month', createAt) = ?
      order by createAt desc"
     year month]))


