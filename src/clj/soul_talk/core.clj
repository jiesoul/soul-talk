(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [soul-talk.config :refer [env]]
            [soul-talk.handler :refer [app]]
            [mount.core :refer [defstate]]
            [mount.core :as mount]
            [taoensso.timbre :as log])
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

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))