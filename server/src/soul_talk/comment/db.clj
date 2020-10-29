(ns soul-talk.comment.db
  (:require [clojure.java.jdbc :as jdbc]
            [soul-talk.database.db :refer [*db*]]))

(defn save-comment! [comment]
  (jdbc/insert! *db* :comments comment))


(defn delete-comment! [id]
  (jdbc/delete! *db* :comments ["id = ? " id]))


(defn get-comments-by-articleId [article-id]
  (jdbc/query *db*
             :comments
             ["select * from comments where article_id = ? order by create_time desc"
              article-id]))

(defn get-comments-by-reply-id [reply-id]
  (jdbc/query *db*
             :comments
             ["select 8 from comments where reply_id = ? order by create_time desc"]))
