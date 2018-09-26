(ns dev
  (:require [soul-talk.models.db :refer [*db* db-spec]]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]
            [mount.core :as mount :refer [defstate]]
            [mount.tools.graph :refer [states-with-deps]]
            [figwheel :refer [start-fw stop-fw cljs]]
            [soul-talk.core]
            [soul-talk.models.db]
            [soul-talk.config]
            [ragtime.jdbc :as jdbc]
            [ragtime.repl :refer [migrate rollback]]
            [selmer.parser :as parser]))

(defn start []
  (mount/start
    #'soul-talk.config/conf
    #'soul-talk.models.db/*db*
    #'soul-talk.core/system))

(def config
  {:datastore  (jdbc/sql-database *db*)
   :migrations (jdbc/load-resources "migrations")})

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
