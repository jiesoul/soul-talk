(ns soul-talk.routes.comment
  (:require [soul-talk.routes.common :refer [handler]]
            [soul-talk.models.comment-db :as comment-db]
            [ring.util.http-response :as resp]))

(handler get-all-comments-by-post-id [post-id]
  (let [comments (comment-db/get-comments-by-post-id post-id)]
    (-> {:result :ok
         :comments comments}
      resp/ok)))
