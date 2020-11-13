(ns soul-talk.database.db-test
  (:require [clojure.test :refer :all]
            [soul-talk.database.db :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [soul-talk.config :refer [conf]]
            [mount.core :as mount]
            [soul-talk.database.my-migrations :as migrations]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'soul-talk.config/conf
      #'soul-talk.database.db/*db*)
    (f)))

(deftest test-db
  (jdbc/with-transaction [t-conn *db*]
    (is (= 1 1))))