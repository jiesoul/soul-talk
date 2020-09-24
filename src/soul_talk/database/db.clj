(ns soul-talk.database.db
  (:require [hikari-cp.core :refer :all]
            [mount.core :refer [defstate]]
            [clojure.java.jdbc :as jdbc]
            [soul-talk.config :refer [env]])
  (:import (java.sql Date Timestamp PreparedStatement)))

(defonce datasource
         (delay (make-datasource {:jdbc-url (:database-url env)})))

(defn create-conn []
  {:datasource @datasource})

(defn close-conn []
  (close-datasource @datasource))

(defstate ^:dynamic *db*
  :start (create-conn)
  :stop (close-conn))

(defn test-db []
  (jdbc/query *db* "select 3*5 as result"))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn

  Date
  (result-set-read-column [v _ _] (to-date v))

  Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt ^long idx]
    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(defn coll-to-in-str [coll]
  (subs
    (reduce #(str %1 "," (str "'" %2 "'")) "" coll) 1))
