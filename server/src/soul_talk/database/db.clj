(ns soul-talk.database.db
  (:require [mount.core :refer [defstate]]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [next.jdbc.result-set :as rs]
            [soul-talk.env :refer [conf]]
            [taoensso.timbre :as log])
  (:import (java.sql Date Timestamp PreparedStatement)
           (com.zaxxer.hikari HikariDataSource)))

(def default-datasource-options
  {:auto-commit        true
   :read-only          false
   :connection-timeout 30000
   :validation-timeout 5000
   :idle-timeout       600000
   :max-lifetime       1800000
   :minimum-idle       10
   :maximum-pool-size  10
   :register-mbeans    false})

(defonce datasource
  (delay
    (connection/->pool HikariDataSource
      (assoc default-datasource-options :jdbcUrl (:database-url conf)))))

(defn connect!
  [conf]
  @datasource)

(defn disconnect!
  [conn]
  (when (and (instance? HikariDataSource conn)
          (not (.isClosed conn)))
    (.close conn)))

(defn reconnect!
  "calls disconnect! to ensure the connection is closed
   then calls connect! to establish a new connection"
  [conn conf]
  (disconnect! conn)
  (connect! conf))

(defstate ^:dynamic *db*
  :start (connect! conf)
  :stop (disconnect! *db*))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol rs/ReadableColumn
  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]
    (to-date v))
  (read-column-by-index [^java.sql.Date v _2 _3]
    (to-date v))
  java.sql.Timestamp
  (read-column-by-label [^java.sql.Timestamp v _]
    (to-date v))
  (read-column-by-index [^java.sql.Timestamp v _2 _3]
    (to-date v)))

;(extend-protocol jdbc/IResultSetReadColumn
;
;  Date
;  (result-set-read-column [v _ _] (to-date v))
;
;  Timestamp
;  (result-set-read-column [v _ _] (to-date v)))

;(extend-type java.util.Date
;  jdbc/ISQLParameter
;  (set-parameter [v ^PreparedStatement stmt ^long idx]
;    (.setTimestamp stmt idx (Timestamp. (.getTime v)))))

(defn coll-to-in-str [coll]
  (subs
    (reduce #(str %1 "," (str "'" %2 "'")) "" coll) 1))
