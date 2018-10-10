(ns soul-talk.models.db
  (:require [clojure.java.jdbc :as sql]
            [hikari-cp.core :refer :all]
            [mount.core :refer [defstate]]
            [clojure.java.jdbc :as jdbc]
            [soul-talk.config :refer [env]]
            [taoensso.timbre :as log])
  (:import (java.sql Date Timestamp PreparedStatement)))

(def datasource-options {:auto-commit true
                         :read-only false
                         :connection-timeout 30000
                         :validation-timeout 5000
                         :idle-timeout 600000
                         :max-lifetime 1800000
                         :minimum-idle 10
                         :maximum-pool-size 10
                         :pool-name "db-pool"
                         :register-mbeans false})

(defonce datasource
         (delay (make-datasource (merge datasource-options (:db-conf env)))))

(defn create-conn []
  (log/info @datasource)
  {:datasource @datasource})

(defn close-conn []
  (close-datasource @datasource))

(defstate ^:dynamic *db*
  :start (create-conn)
  :stop (close-conn))

(defn test-db []
  (sql/query *db* "select 3*5 as result"))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol sql/IResultSetReadColumn

  Date
  (result-set-read-column [v _ _] (to-date v))

  Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt ^long idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))
