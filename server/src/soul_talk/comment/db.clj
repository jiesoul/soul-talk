(ns soul-talk.comment.db
  (:require [next.jdbc.sql :as sql]
            [soul-talk.database.db :refer [*db*]]
            [next.jdbc.result-set :as rs-set]))

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
