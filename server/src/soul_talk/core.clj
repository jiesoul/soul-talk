(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            [soul-talk.config :refer [env]]
            [soul-talk.handler :refer [app]]
            [mount.core :as mount :refer [defstate]]
            [soul-talk.env :refer [defaults]]
            [clojure.tools.cli :refer [parse-opts]]
            [soul-talk.database.my-migrations :as migrations]
            [taoensso.timbre :as log])
  (:gen-class))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]])

(defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(defn start-system []
  ;(log/info "total config: " env)
  (-> #'app
      (jetty/run-jetty
        (-> env
          (update :port #(or (-> env :options :port) %))
          (assoc :join? false)))))

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
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)])
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (cond
    (some #{"migrate" "rollback"} args)
    (do
      (mount/start #'soul-talk.config/env)
      (migrations/migrate args (select-keys env [:database-url :migrations]))
      (System/exit 0))
    :else
    (start-app args)))
