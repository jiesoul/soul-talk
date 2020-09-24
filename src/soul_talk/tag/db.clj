(ns soul-talk.tag.db
  (:require [soul-talk.database.db :refer [*db* coll-to-in-str]]
            [clojure.java.jdbc :as jdbc]
            [taoensso.timbre :as log]))

(defn save-tag! [tag]
  (jdbc/insert! *db* :tags tag))

(defn delete-tag! [id]
  (jdbc/delete! *db* :tags ["id = ?" id]))

(defn get-tags []
  (jdbc/query *db* ["select * from tags"]))

(defn get-tag-by-id [id]
  (jdbc/get-by-id *db* :tags id))

(defn get-tag-by-name [name]
  (first
    (jdbc/query *db* ["select * from tags where name = ?" name])))

(defn tags-with-names [tag-names]
  (let [tag-str (coll-to-in-str tag-names)]
    (jdbc/query *db*
      [(str "select * from tags where name in (" tag-str ")")])))

(defn add-tags-to-article [article-id tag-names]
  (when-not (empty? tag-names)
    (let [tags (tags-with-names tag-names)
          inputs (mapv #(hash-map :articleId article-id :tagId (:id %)) tags)]
      (jdbc/insert-multi! *db* :articleTags inputs))))

(defn get-tag-by-article-id [id]
  (jdbc/query *db*
    ["select * from tags where id in (select tagId from articleTags where articleId = ?)" id]))
