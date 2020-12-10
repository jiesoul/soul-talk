(ns soul-talk.database.db
  (:require [mount.core :refer [defstate]]
            [next.jdbc.prepare :as p]
            [next.jdbc.connection :as connection]
            [next.jdbc.result-set :as rs]
            [soul-talk.config :refer [conf]]
            [clojure.tools.logging :as log]
            [next.jdbc :as jdbc])
  (:import (java.sql Date Timestamp PreparedStatement)
           (com.zaxxer.hikari HikariDataSource)
           (java.time Instant LocalDate LocalDateTime)))

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

(defn- disconnect!
  [conf conn]
  (let [uri (get conf :database-url)]
    (log/info "disconnecting from " uri)
    (when (and (instance? HikariDataSource conn)
            (not (.isClosed conn)))
      (.close conn))))

(defn- reconnect!
  "calls disconnect! to ensure the connection is closed
   then calls connect! to establish a new connection"
  [conn conf]
  (disconnect! conf conn)
  (connect! conf))

(defn- new-connection! [conf]
  (let [uri (get conf :database-url)]
    (jdbc/with-options
      (connection/->pool HikariDataSource
        (assoc default-datasource-options :jdbcUrl uri))
      {:builder-fn rs/as-unqualified-maps})))


(defstate ^:dynamic *db*
  :start (new-connection! conf)
  :stop (disconnect! conf *db*))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol rs/ReadableColumn
  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]
    (to-date v))
  (read-column-by-index [^java.sql.Date v _2 _3]
    (to-date v))
  Timestamp
  (read-column-by-label [^Timestamp v _]
    (to-date v))
  (read-column-by-index [^Timestamp v _2 _3]
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

(extend-protocol p/SettableParameter
  Instant
  (set-parameter [^Instant v ^PreparedStatement ps ^long i]
    (.setTimestamp ps i (Timestamp/from v)))
  LocalDate
  (set-parameter [^LocalDate v ^PreparedStatement ps ^long i]
    (.setTimestamp ps i (Timestamp/valueOf (.atStartOfDay v))))
  LocalDateTime
  (set-parameter [^LocalDateTime v ^PreparedStatement ps ^long i]
    (.setTimestamp ps i (Timestamp/valueOf v))))

(defn coll-to-in-str [coll]
  (subs
    (reduce #(str %1 "," (str "'" %2 "'")) "" coll) 1))

