(ns soul-talk.routes.posts
  (:require [compojure.core :refer [GET POST context defroutes]]
            [soul-talk.models.post-db :as post-db]
            [ring.util.http-response :as resp]
            [taoensso.timbre :as log]
            [clojure.spec.alpha :as s]
            [java-time.local :as l]
            [java-time.format :as f]
            [soul-talk.routes.common :refer [handler]]
            [soul-talk.validate :refer [post-errors]]
            [soul-talk.pagination :as p]))

(s/def ::id string?)
(s/def ::img_url string?)
(s/def ::title string?)
(s/def ::content string?)
(s/def ::publish int?)
(s/def ::category int?)
(s/def ::author string?)

(def Post (s/def ::Post (s/keys :req-un [::title ::content ::publish ::author]
                                :opt-un [::category])))

(def format-id (java-time.format/formatter "yyyyMMddHHmmssSSS"))

(handler get-all-posts [request]
         (let [pagination (p/create request)
               posts (post-db/get-posts-page pagination)
               total (post-db/count-posts-all)
               pagination (p/create-total pagination total)]
           (resp/ok {:result :ok
                     :posts  posts
                     :pagination pagination})))

(handler get-publish-posts [req]
         (let [pagination (p/create req)
               posts (post-db/get-posts-publish-page pagination)
               total (post-db/count-posts-publish)]
           (resp/ok {:result :ok
                     :posts posts
                     :pagination (assoc pagination :total total)})))

(handler get-post [post-id]
         (let [post (post-db/get-post-by-id post-id)]
           (resp/ok {:result :ok
                     :post post})))

(handler save-post! [post]
         (let [error (post-errors post)
               time (l/local-date-time)
               id (f/format format-id time)]
           (if error
             (resp/unauthorized {:result :error
                                 :message error})
             (do
               (post-db/save-post! (-> post
                                       (assoc :id id)
                                       (assoc :create_time time)
                                       (assoc :modify_time time)))
               (-> {:result :ok}
                   (resp/ok))))))

(handler update-post! [post]
         (post-db/update-post! (-> post
                                   (assoc :modify_time (l/local-date-time))))
         (-> {:result :ok
              :post   (assoc post :content nil)}
             (resp/ok)))

(handler delete-post! [id]
         (do
           (post-db/delete-post! id)
           (resp/ok {:result :ok})))

(handler publish-post! [id]
         (do
           (post-db/publish-post! id)
           (-> {:result :ok}
               (resp/ok))))

(handler get-posts-archives []
         (let [archives (post-db/get-posts-archives)]
           (-> {:result :ok
                :archives archives}
               resp/ok)))