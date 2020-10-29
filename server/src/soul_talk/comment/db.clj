(ns soul-talk.comment.db
  (:require [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]))

(defn save-comment! [comment]
  (sql/insert! *db* :comments comment))


(defn delete-comment! [id]
  (sql/delete! *db* :comments ["id = ? " id]))


(defn get-comments-by-articleId [article-id]
  (sql/query *db*
             :comments
             ["select * from comments where article_id = ? order by create_time desc"
              article-id]))

(defn get-comments-by-reply-id [reply-id]
  (sql/query *db*
             :comments
             ["select 8 from comments where reply_id = ? order by create_time desc"]))
