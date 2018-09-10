(ns soul-talk.routes.post
  (:require [compojure.core :refer [GET POST context defroutes]]
            [soul-talk.models.post-db :as post-db]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]))

(defn list-posts []
  (let [posts (post-db/get-posts)]
    (resp/ok {:result :ok
              :posts  posts})))

(defn get-post [post-id]
  (let [post (post-db/get-post post-id)]
    (resp/ok {:result :ok
              :post post})))

(defroutes
  post-routes
  (context
    "/posts" []
    (GET "/" [] (list-posts))
    (GET "/:post-id" [post-id] (get-post post-id))))