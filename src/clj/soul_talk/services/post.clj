(ns soul-talk.services.post
  (:require [soul-talk.handlers.posts :as posts]
            [soul-talk.handlers.auth :refer [authenticated]]
            [compojure.api.sweet :refer [api context GET POST PUT DELETE]]))

(def routes
  (context "/posts" []

    (GET "/" req
      :return ::Result
      :summary "load all publish posts"
      (posts/get-publish-posts req))

    (GET "/archives" []
      :return ::Result
      :summary "load post archives"
      (posts/get-posts-archives))

    (GET "/archives/:year/:month" []
      :return ::Result
      :path-params [year :- int? month :- int?]
      :summary "load post archives"
      (posts/get-posts-archives-year-month year month))

    (GET "/:id" [id]
      :return ::Result
      :summary "load post"
      (posts/get-post id))

    ;;auth
    (GET "/" request
      :auth-rules authenticated
      :return ::Result
      :summary "return all posts contains id not publish"
      (posts/get-all-posts request))

    (POST "/" []
      :auth-rules authenticated
      :return ::Result
      :body [post posts/Post]
      :summary "add a new post"
      (posts/save-post! post))

    (PUT "/:id" []
      :auth-rules authenticated
      :return ::Result
      :body [post posts/Post]
      :summary "update a post"
      (posts/update-post! post))

    (DELETE "/:id" [id]
      :auth-rules authenticated
      :return ::Result
      :summary "delete a post"
      (posts/delete-post! id))

    (PUT "/:id/publish" [id]
      :auth-rules authenticated
      :return ::Result
      :summary "publish a post"
      (posts/publish-post! id))

    (POST "/upload" req
      :auth-rules authenticated
      :return ::Result
      :summary "upload md file as post"
      (posts/upload-post! req))))
