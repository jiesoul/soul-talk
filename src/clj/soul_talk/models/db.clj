(ns soul-talk.models.db
  (:require [clojure.java.jdbc :as sql]
            [hikari-cp.core :refer :all]
            [taoensso.timbre :as log]
            [mount.core :refer [defstate]])
  (:import (java.sql Date)))

(def datasource-options {:auto-commit true
                         :read-only false
                         :connection-timeout 30000
                         :validation-timeout 5000
                         :idle-timeout 600000
                         :max-lifetime 1800000
                         :minimum-idle 10
                         :maximum-pool-size 10
                         :pool-name "db-pool"
                         :adapter "postgresql"
                         :username "jiesoul"
                         :password "12345678"
                         :database-name "soul_talk"
                         :server-name "localhost"
                         :port-number 5432
                         :register-mbeans false})

(defonce datasource
         (delay (make-datasource datasource-options)))

(defn create-conn []
  {:datasource @datasource})

(defn close-conn []
  (close-datasource @datasource))

(def db-spec {:datasource @datasource})

(defstate ^:dynamic *db*
  :start (create-conn)
  :stop (close-conn))

(defn test-db []
  (sql/query *db* "select 3*5 as result"))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))
