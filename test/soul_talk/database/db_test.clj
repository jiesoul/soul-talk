(ns soul-talk.database.db-test
  (:require [clojure.test :refer :all]
            [soul-talk.database.db :refer :all]
            [clojure.java.jdbc :as jdbc]
            [soul-talk.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'soul-talk.config/env
      #'soul-talk.database.db/*db*)
    (f)))

(deftest db-test
  (testing "datasource connection"
    (jdbc/with-db-transaction [t-conn *db*]
      (jdbc/db-set-rollback-only! t-conn)
      (test
        (is (= 15 (:result (first (test-db)))))))))