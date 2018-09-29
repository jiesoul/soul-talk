(ns soul-talk.handler
  (:require [soul-talk.middleware :as middleware]
            [soul-talk.layout :as layout]
            [soul-talk.env :refer [defaults]]
            [compojure.route :as route]
            [compojure.core :refer [routes wrap-routes]]
            [soul-talk.routes.home :refer [home-routes auth-routes]]
            [soul-talk.routes.services :refer [services-routes]]
            [mount.core :as mount]))

(mount/defstate init-app
  :start (or (:init defaults) identity)
  :stop (or (:init defaults) identity))

(mount/defstate app
  :start
  (-> (routes
        services-routes
        (-> #'home-routes
          (wrap-routes middleware/wrap-csrf))
        (-> #'auth-routes
          (wrap-routes middleware/wrap-csrf)
          (wrap-routes middleware/wrap-session-auth))
        (route/not-found
          (:body
            (layout/error-page
              {:status 404
               :title  "页面未找到"}))))
    (middleware/wrap-base)))