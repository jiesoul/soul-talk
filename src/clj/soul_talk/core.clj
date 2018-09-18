(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [routes wrap-routes]]
            [compojure.route :as route]
            [soul-talk.routes.home :refer [home-routes]]
            [soul-talk.routes.services :refer [services-routes]]
            [soul-talk.middleware :as middleware]
            [soul-talk.layout :as layout]
            [mount.core :refer [defstate]])
  (:gen-class))

(def app
  (-> (routes
        services-routes
        (wrap-routes #'home-routes middleware/wrap-csrf)
        (route/not-found (:body
                           (layout/error-page {:status 404
                                                  :title "页面未找到"}))))
      (middleware/wrap-base)))

(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))

(defn start-system []
  (-> app
      (jetty/run-jetty
        {:port 3000
         :join? false})))

(defstate system :start (start-system)
          :stop (.stop system))