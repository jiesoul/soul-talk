(ns soul-talk.models.comment-db
  (:require [clojure.java.jdbc :as sql]
            [soul-talk.models.db :refer [*db*]]))

(defn save-comment! [comment]
  (sql/insert! *db* :comments comment))


(defn delete-comment! [id]
  (sql/delete! *db* :comments ["id = ? " id]))


(defn get-comments-by-post-id [post-id]
  (sql/query *db*
             :comments
             ["select * from comments where id = ? order by create_time desc"
              post-id]))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
             :comments
             ["select 8 from comments where reply_id = ? order by create_time desc"]))
