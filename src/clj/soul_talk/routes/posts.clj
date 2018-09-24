(ns soul-talk.routes.posts
  (:require [compojure.core :refer [GET POST context defroutes]]
            [soul-talk.models.post-db :as post-db]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.routes.common :refer [handler]]
            [soul-talk.validate :refer [post-errors]]))

(s/def ::id string?)
(s/def ::img_url string?)
(s/def ::title string?)
(s/def ::content string?)
(s/def ::publish int?)
(s/def ::category string?)
(s/def ::author string?)

(def Post (s/def ::Post (s/keys :req-un [::title ::content ::publish ::author]
                                :opt-un [::category])))

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(handler get-all-posts []
  (let [posts (post-db/get-posts-all)]
    (resp/ok {:result :ok
              :posts  posts})))

(handler get-post [post-id]
  (let [post (post-db/get-post-by-id post-id)]
    (resp/ok {:result :ok
              :post post})))

(handler save-post! [post]
  (let [error (post-errors post)
        time (l/local-date-time)
        id (f/format format-id time)
        category (Integer/parseInt (:category post))]
    (if error
      (resp/unauthorized {:result :error
                          :message error})
      (do
        (post-db/save-post! (-> post
                                (assoc :id id)
                                (assoc :category category)
                                (assoc :create_time time)
                                (assoc :modify_time time)))
        (-> {:result :ok}
            (resp/ok))))))

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