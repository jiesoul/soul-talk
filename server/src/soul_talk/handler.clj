(ns soul-talk.handler
  (:require [compojure.route :as route]
            [compojure.core :refer [routes]]
            [soul-talk.rest-api.api :refer [public-api-routes private-api-routes]]
            [soul-talk.rest-api.middleware :as m]))

(def app
  (-> (routes
        (m/wrap-base public-api-routes)
        (m/wrap-base private-api-routes)
        (route/resources "/")
        (route/not-found
          {:status 404
           :title  "无效的资源"}))))