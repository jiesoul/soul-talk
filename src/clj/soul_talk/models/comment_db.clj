(ns soul-talk.models.comment-db
  (:require [clojure.java.jdbc :as sql]
            [soul-talk.models.db :refer [db-spec]]))

(defn save-comment! [comment]
  (sql/insert! db-spec :comments comment))


(defn delete-comment! [id]
  (sql/delete! db-spec :comments ["id = ? " id]))


(defn get-comments-by-post-id [post-id]
  (sql/query db-spec
             :comments
             ["select * from comments where id = ? order by create_time desc"
              post-id]))
