(ns soul-talk.core
  (:require [ring.adapter.jetty :as jetty]
            ;[clojure.tools.nrepl.server :refer [start-server stop-server]]
            [soul-talk.handler :refer [app]]
            [soul-talk.env :refer [defaults]]
            [soul-talk.config :refer [conf]]
            [clojure.tools.cli :refer [parse-opts]]
            [cprop.core :refer [load-config]]
            [soul-talk.database.my-migrations :as migrations]
            [taoensso.timbre :as log]
            [mount.core :as mount :refer [args defstate]])
  (:gen-class))

;;; example on creating a network REPL
;(defn- start-nrepl [{:keys [host port]}]
;  (start-server :bind host :port port))
;
;;; nREPL is just another simple state
;(defstate nrepl :start (start-nrepl (:nrepl conf))
;  :stop (stop-server nrepl))

(defn update-db [conf]
  (migrations/migrate ["migrate"] (select-keys conf [:database-url :migrations])))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]])

(defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(defn start-system [conf]
  (log/info "total config: " conf)
  (update-db conf)
  (-> #'app
      (jetty/run-jetty
        (-> conf
          (update :port #(or (-> conf :options :port) %))
          (assoc :join? false)))))

(defstate system  :start (start-system conf)
                  :stop (.stop system))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (do
    (doseq [component (-> (parse-opts args cli-options)
                        mount/start-with-args
                        :started)]
      (log/info component " started"))
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app))))

(defn -main [& args]
  (start-app args))
