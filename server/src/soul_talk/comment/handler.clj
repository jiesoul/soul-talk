(ns soul-talk.comment.handler
  (:require [soul-talk.base.common :refer [handler]]
            [soul-talk.comment.db :as comment-db]
            [ring.util.http-response :as resp]))

(defn get-comments-by-articleId [articleId]
  (let [comments (comment-db/get-comments-by-articleId articleId)]
    (resp/ok {:result :ok
              :data   {:comments comments}})))
