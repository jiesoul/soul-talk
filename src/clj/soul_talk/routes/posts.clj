(ns soul-talk.routes.posts
  (:require [compojure.core :refer [GET POST context defroutes]]
            [soul-talk.models.post-db :as post-db]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]
            [java-time.local :as l]
            [soul-talk.routes.common :refer [handler]]))

(s/def ::id string?)
(s/def ::img_url string?)
(s/def ::title string?)
(s/def ::content string?)
(s/def ::publish int?)
(s/def ::category int?)
(s/def ::author string?)

(def Post (s/def ::Post (s/keys :req-un [::title ::content ::publish ::category ::author]
                                :opt-un [::category])))

(handler get-all-posts []
  (let [posts (post-db/get-posts-all)]
    (resp/ok {:result :ok
              :posts  posts})))

(handler get-post [post-id]
  (let [post (post-db/get-post-by-id post-id)]
    (resp/ok {:result :ok
              :post post})))

(handler save-post! [post]
  (let [time (l/local-date-time)]
    (post-db/save-post! (-> post
                          (assoc :create_time time)
                          (assoc :modify_time time)
                          (assoc :publish 0)))
    (-> {:result :ok}
      (resp/ok))))

(handler update-post! [post]
  (do
    (post-db/update-post! (-> post
                              (assoc :modify_time (l/local-date-time))))
    (-> {:result :ok}
      (resp/ok))))

(handler publish-post! [post]
  (do
    (post-db/publish-post! (assoc post :modify_time (l/local-date-time)))
    (-> {:result :ok}
      (resp/ok))))

(defroutes
  post-routes
  (context
    "/posts" []
    (GET "/" [] (get-all-posts))
    (GET "/:post-id" [post-id] (get-post post-id))))