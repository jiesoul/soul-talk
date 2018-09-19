(ns dev
  (:require [soul-talk.models.db :as db :refer [db-spec]]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount :refer [defstate]]
            [mount.tools.graph :refer [states-with-deps]]
            [figwheel :refer [start-fw stop-fw cljs]]
            [soul-talk.core]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :as rig-repl :refer [migrate rollback]]))

(def config
  {:datastore  (jdbc/sql-database db-spec)
   :migrations (jdbc/load-resources "migrations")})

(tn/set-refresh-dirs "src" "dev")

(defn start []
  (mount/start
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
