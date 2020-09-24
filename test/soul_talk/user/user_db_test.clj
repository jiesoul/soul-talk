(ns soul-talk.user.user-db-test
  (:require [clojure.test :refer :all]
            [soul-talk.database.db :refer :all]
            [soul-talk.database.db-test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [soul-talk.user.db :as user-db]))

(deftest user-db-test
  (testing "user login"
    (jdbc/with-db-transaction [t-conn *db*]
      (jdbc/db-set-rollback-only! t-conn)
      (test
        (is (= 1 (count (user-db/select-all-users))))))))
