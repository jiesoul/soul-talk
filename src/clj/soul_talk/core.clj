(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [soul-talk.handler :refer [app]]
            [mount.core :refer [defstate]])
  (:gen-class))

(defn start-system []
  (-> #'app
      (jetty/run-jetty
        {:port 3000
         :join? false})))

(defstate ^{:on-reload :noop}
  system
  :start (start-system)
  :stop (.stop system))


(defn -main []
  (jetty/run-jetty
    app
    {:port 3000
     :join? false}))