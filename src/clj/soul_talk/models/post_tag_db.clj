(ns soul-talk.models.post-tag-db
  (:require [soul-talk.models.db :refer [*db*]]
            [clojure.java.jdbc :as sql]))

(defn save-post-tags! [posts-tags]
  (sql/insert-multi! *db*
                     :posts-tags
                     posts-tags))

(defn delete-post-tag-by-post-id [post-id]
  (sql/delete! *db* :posts-tags ["post_id = ?" post-id]))


(defn delete-post-tag-by-tag-id [tag-id]
  (sql/delete! *db* :posts-tags ["tag_id = ?" tag-id]))
