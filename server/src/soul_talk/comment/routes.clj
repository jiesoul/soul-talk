(ns soul-talk.comment.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.spec.core :refer [Result]]))

(def comment-routes
  (context "/comments" []
    :tags ["评论"]
    (POST "/" []
      :return Result)))