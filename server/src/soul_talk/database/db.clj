(ns soul-talk.database.db
  (:require [mount.core :refer [defstate]]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [next.jdbc.result-set :as rs]
            [soul-talk.config :refer [env]]
            [mount.core :as mount]
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

(def options
  (assoc default-datasource-options :jdbcUrl (:database-url env)))

(defonce datasource
  (delay
    (connection/->pool HikariDataSource
      (assoc default-datasource-options :jdbcUrl (:database-url env)))))

(defn connect!
  [options]
  (log/info "datasource option: " options)
  (let [ds (delay (connection/->pool HikariDataSource {:jdbcUrl (:database-url)}))]
    @datasource))

(defn disconnect!
  [conn]
  (when (and (instance? HikariDataSource conn)
          (not (.isClosed conn)))
    (.close conn)))

(defn reconnect!
  "calls disconnect! to ensure the connection is closed
   then calls connect! to establish a new connection"
  [conn options]
  (disconnect! conn)
  (connect! options))

(defn create-conn []
    @datasource)

(defn close-conn []
  (.close @datasource))

(defstate ^:dynamic *db*
  :start (connect! options)
  :stop (disconnect! *db*))

(defn test-db []
  (let [sql "select 3*5 as result"]
    (log/info (str "执行 DB 操作 " sql " 查询结果为: ")
      (jdbc/execute! *db* "select 3*5 as result"))))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol rs/ReadableColumn
  java.sql.Date
  (read-column-by-label [^java.sql.Date v _]
    (.toLocalDate v))
  (read-column-by-index [^java.sql.Date v _2 _3]
    (.toLocalDate v))
  java.sql.Timestamp
  (read-column-by-label [^java.sql.Timestamp v _]
    (.toInstant v))
  (read-column-by-index [^java.sql.Timestamp v _2 _3]
    (.toInstant v)))

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
