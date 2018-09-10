(ns soul-talk.models.db
  (:require [clojure.java.jdbc :as sql]
            [taoensso.timbre :as log]))

(def db-spec {:subprotocol "postgresql"
              :subname "//localhost:5432/soul_talk"
              :user "jiesoul"
              :password "12345678"})

(defn test-db []
  (sql/query db-spec "select 3*5 as result"))

