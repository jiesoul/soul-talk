(ns soul-talk.handler
  (:require [compojure.route :as route]
            [compojure.core :refer [routes]]
            [soul-talk.api :as api]
            [soul-talk.site :as site]
            [soul-talk.middleware :as m]))

(def app
  (-> (routes
        api/api-routes
        site/site-routes
        (route/resources "/")
        (route/not-found
          {:status 404
           :title  "无效的资源"}))
    (m/wrap-base)))