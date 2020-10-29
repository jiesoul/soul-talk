(ns soul-talk.tag.db
  (:require [soul-talk.database.db :refer [*db* coll-to-in-str]]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [taoensso.timbre :as log]))

(defn save-tag! [tag]
  (sql/insert! *db* :tags tag))

(defn delete-tag! [id]
  (sql/delete! *db* :tags ["id = ?" id]))

(defn get-tags []
  (sql/query *db* ["select * from tags"]))

(defn get-tag-by-id [id]
  (sql/get-by-id *db* :tags id))

(defn get-tag-by-name [name]
  (first
    (sql/query *db* ["select * from tags where name = ?" name])))

(defn tags-with-names [tag-names]
  (let [tag-str (coll-to-in-str tag-names)]
    (sql/query *db*
      [(str "select * from tags where name in (" tag-str ")")])))

(defn add-tags-to-article [article-id tag-names]
  (when-not (empty? tag-names)
    (let [tags (tags-with-names tag-names)
          inputs (mapv #(hash-map :articleId article-id :tagId (:id %)) tags)]
      (sql/insert-multi! *db* :articleTags inputs))))

(defn get-tag-by-article-id [id]
  (sql/query *db*
    ["select * from tags where id in (select tag_id from article_tags where article_id = ?)" id]))
