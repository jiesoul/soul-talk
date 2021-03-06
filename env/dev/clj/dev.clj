(ns dev
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]
            [soul-talk.config :refer [env]]
            [soul-talk.models.db :refer [*db*]]
            [soul-talk.core]
            [mount.core :as mount]
            [mount-up.core :as mu]
            [soul-talk.my-migrations :as my-migrations]))

(defn migrate [args]
  (my-migrations/migrate args (select-keys env [:database-url :migrations])))

(mu/on-upndown :info mu/log :before)

(defn start []
  (mount/start
    #'soul-talk.config/env
    #'soul-talk.core/init-app
    #'soul-talk.models.db/*db*
    #'soul-talk.core/system))

(defn stop []
  (mount/stop))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stop all states, reloads modified source files"
  []
  (stop)
  (tn/refresh :after 'dev/go))

(mount/in-clj-mode)
