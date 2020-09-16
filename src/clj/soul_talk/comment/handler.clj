(ns soul-talk.comment.handler
  (:require [soul-talk.base.common :refer [handler]]
            [soul-talk.models.db :as comment-db]
            [ring.util.http-response :as resp]))

(handler get-all-comments-by-post-id [post-id]
  (let [comments (comment-db/get-comments-by-post-id post-id)]
    (-> {:result :ok
         :comments comments}
      resp/ok)))
