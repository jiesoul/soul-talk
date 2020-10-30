(ns dev
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]
            [soul-talk.env :refer [conf]]
            [soul-talk.database.db :refer [*db*]]
            [soul-talk.core]
            [mount.core :as mount]
            [mount-up.core :as mu]
            [soul-talk.database.my-migrations :as my-migrations]
            [com.jakemccrary.test-refresh :as test-refresh]
            [taoensso.timbre :as log]))

(defn migrate [args]
  (log/info "migrate args: " args)
  (log/info "conf: " conf)
  (my-migrations/migrate args (select-keys conf [:database-url :migrations])))

(mu/on-upndown :info mu/log :before)

(defn start []
  (mount/start
    #'soul-talk.env/conf
    #'soul-talk.database.db/*db*
    #'soul-talk.core/init-app
    #'soul-talk.core/system))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

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
