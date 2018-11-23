(ns soul-talk.handler
  (:require [soul-talk.middleware :as middleware]
            [soul-talk.layout :as layout]
            [soul-talk.env :refer [defaults]]
            [compojure.route :as route]
            [compojure.core :refer [routes wrap-routes]]
            [soul-talk.routes.home :refer [home-routes auth-routes]]
            [soul-talk.routes.services :refer [services-routes]]))

(def app
  (-> (routes
        (-> #'auth-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-session-auth))
        (-> #'home-routes
          (wrap-routes middleware/wrap-csrf))
        services-routes
        (route/not-found
          (:body
            (layout/error-page
              {:status 404
               :title  "页面未找到"}))))
    (middleware/wrap-base)))