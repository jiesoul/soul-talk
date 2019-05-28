(ns soul-talk.handler
  (:require [soul-talk.middleware :as middleware]
            [soul-talk.env :refer [defaults]]
            [compojure.route :as route]
            [compojure.core :refer [routes wrap-routes]]
            [soul-talk.routes.home :refer [home-routes auth-routes]]
            [soul-talk.routes.services :refer [services-routes]]))

(def app
  (-> (routes
        services-routes
        (route/not-found
          {:status 404
           :title  "页面未找到"}))
    (middleware/wrap-base)))