(ns soul-talk.test.models.post-db-test
  (:require [clojure.test :refer :all]
            [soul-talk.models.post-db :refer :all]
            [soul-talk.models.db :refer [db-spec]]
            [clojure.java.jdbc :as sql]
            [java-time.local :as l]))

(def post {:id "201809201450333"
           :img_url ""
           :title "这一是个测试 post "
           :content ""
           :create_time (l/local-date-time)
           :modify_time (l/local-date-time)
           :category 1
           :publish 0
           :author "jiesoul"
           :counter 0})

(deftest test-posts
  (sql/with-db-transaction [t-con db-spec]
    (sql/db-set-rollback-only! t-con)
    (is (= 1 (count (save-post! post))))
    (is (= "201809201450333" (:id (get-post "201809201450333"))))
    (is (= 1 (first (delete-post! (:id post)))))))

