(ns soul-talk.handler
  (:require [soul-talk.middleware :as middleware]
            [compojure.route :as route]
            [compojure.core :refer [routes]]
            [soul-talk.services :refer [services-routes]]))

(def app
  (-> (routes
        services-routes
        (route/not-found
          {:status 404
           :title  "无效的资源"}))
    (middleware/wrap-base)))