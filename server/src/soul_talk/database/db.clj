(ns soul-talk.database.db
  (:require [mount.core :refer [defstate]]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection]
            [soul-talk.config :refer [env]]
            [mount.core :as mount]
            [taoensso.timbre :as log])
  (:import (java.sql Date Timestamp PreparedStatement)
           (com.zaxxer.hikari HikariDataSource)))

(def db-spec {:dbtype "postgresql"
              :jdbcUrl (:database-url env)
              :connectionInitSql "COMMIT"
              :maximumPoolSize 15})

(def datasource (atom nil))

(defn create-conn []
  (let [ds (connection/->pool HikariDataSource db-spec)]
    (reset! datasource ds)
    @datasource))

(defn close-conn []
  (.close @datasource))

(defstate ^:dynamic *db*
  :start (create-conn)
  :stop (close-conn))

(defn test-db []
  (let [sql "select 3*5 as result"]
    (log/info (str "执行 DB 操作 " sql " 查询结果为: ")
      (jdbc/execute! *db* "select 3*5 as result"))))

(defn to-date [^Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

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
