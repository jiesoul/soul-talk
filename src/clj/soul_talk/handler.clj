(ns soul-talk.handler
  (:require [compojure.route :as route]
            [compojure.core :refer [routes]]
            [soul-talk.rest-api.api :refer [api-routes]]
            [soul-talk.rest-api.middleware :as m]))

(def app
  (-> (routes
        api-routes
        (route/not-found
          {:status 404
           :title  "无效的资源"}))
    (m/wrap-base)))