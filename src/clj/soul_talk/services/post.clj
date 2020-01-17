(ns soul-talk.services.post
  (:require [soul-talk.handlers.posts :as posts]
            [buddy.auth :refer [authenticated?]]
            [soul-talk.handlers.common :as common]
            [compojure.api.sweet :refer [context routes GET POST PUT DELETE]]))

(def post-routes
  (context "/posts" []

    (GET "/" req
      :return ::common/Result
      :summary "load all publish posts"
      (posts/get-publish-posts req))

    (GET "/archives" []
      :return ::common/Result
      :summary "load post archives"
      (posts/get-posts-archives))

    (GET "/archives/:year/:month" []
      :return ::common/Result
      :path-params [year :- int? month :- int?]
      :summary "load post archives"
      (posts/get-posts-archives-year-month year month))

    (GET "/:id" [id]
      :return ::common/Result
      :summary "load post"
      (posts/get-post id))

    ;;auth
    (routes
      :auth-rules authenticated?
      (GET "/" request
        :return ::common/Result
        :summary "return all posts contains id not publish"
        (posts/get-all-posts request))

      (POST "/" []
        :return ::common/Result
        :body [post posts/Post]
        :summary "add a new post"
        (posts/save-post! post))

      (PUT "/:id" []
        :return ::common/Result
        :body [post posts/Post]
        :summary "update a post"
        (posts/update-post! post))

      (DELETE "/:id" [id]
        :return ::common/Result
        :summary "delete a post"
        (posts/delete-post! id))

      (PUT "/:id/publish" [id]
        :return ::common/Result
        :summary "publish a post"
        (posts/publish-post! id))

      (POST "/upload" req
        :return ::common/Result
        :summary "upload md file as post"
        (posts/upload-post! req)))))
