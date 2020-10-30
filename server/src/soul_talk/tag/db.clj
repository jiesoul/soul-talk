(ns soul-talk.tag.db
  (:require [soul-talk.database.db :refer [*db* coll-to-in-str]]
            [next.jdbc.result-set :as rs-set]
            [next.jdbc.sql :as sql]
            [taoensso.timbre :as log]))

(defn save-tag! [tag]
  (sql/insert! *db* :tags tag {:builder-fn rs-set/as-unqualified-maps}))

(defn delete-tag! [id]
  (sql/delete! *db* :tags ["id = ?" id]))

(defn get-tags []
  (sql/query *db* ["select * from tags"] {:builder-fn rs-set/as-unqualified-maps}))

(defn get-tag-by-id [id]
  (sql/get-by-id *db* :tags id))

(defn get-tag-by-name [name]
  (first
    (sql/query *db* ["select * from tags where name = ?" name] {:builder-fn rs-set/as-unqualified-maps})))

(defn tags-with-names [tag-names]
  (let [tag-str (coll-to-in-str tag-names)]
    (sql/query *db*
      [(str "select * from tags where name in (" tag-str ")")] {:builder-fn rs-set/as-unqualified-maps})))

(defn add-tags-to-article [article-id tag-names]
  (when-not (empty? tag-names)
    (if-let [tags (tags-with-names tag-names)]
      (let [inputs (map vector (repeat article-id) (map :id tags))]
        (sql/insert-multi! *db* :article_tags [:article_id :tag_id] inputs)))))

(defn get-tag-by-article-id [id]
  (sql/query *db*
    ["select * from tags where id in (select tag_id from article_tags where article_id = ?)" id]
    {:builder-fn rs-set/as-unqualified-maps}))
