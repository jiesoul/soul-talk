(ns dev
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as tn]
            [soul-talk.config :refer [conf]]
            [soul-talk.database.db :refer [*db*]]
            [soul-talk.core]
            [mount-up.core :as mu]
            [soul-talk.database.my-migrations :as my-migrations]
            [com.jakemccrary.test-refresh :as test-refresh]
            [eftest.runner :refer [find-tests run-tests]]
            [mount.core :as mount]))

(mu/on-upndown :info mu/log :before)

(def test-refresh test-refresh/run-in-repl)

(defn run-test [url & opt]
  (run-tests (find-tests url) opt))

(defn migrate [args]
  (my-migrations/migrate args (select-keys conf [:database-url :migrations])))


(defn start []
  (mount/start
    #'soul-talk.config/conf
    #'soul-talk.database.db/*db*
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
