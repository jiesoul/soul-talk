(ns soul-talk.test.models.db-test
  (:require [clojure.test :refer :all]
            [soul-talk.models.post-db :refer :all]
            [soul-talk.models.user-db :refer :all]
            [soul-talk.models.db :as db :refer [*db*]]
            [clojure.java.jdbc :as sql]
            [soul-talk.config]
            [java-time.local :as l]
            [mount.core :as mount]
            [buddy.hashers :as hashers]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'soul-talk.config/env
      #'soul-talk.models.db/*db*)
    (f)))

(def user {:email "test@gmail.com"
           :password (hashers/encrypt "12345678")})

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

(deftest test-db

  (testing "user test"
    (sql/with-db-transaction [t-con *db*]
      (sql/db-set-rollback-only! t-con)
      (is (= 1 (count (save-user! user))))
      (is (= 1 (update-login-time (assoc user :last_login (l/local-date-time)))))
      (is (= 1 (change-pass! (assoc user :password (hashers/encrypt "11234567890")))))
      (is (= 1 (save-user-profile! (assoc user :name "test"))))))


  (testing "post test"
    (sql/with-db-transaction [t-con *db*]
      (sql/db-set-rollback-only! t-con)
      (is (= 1 (count (save-post! post))))
      (is (= "201809201450333" (:id (get-post-by-id "201809201450333"))))
      (is (= 1 (update-post! (assoc post :img_url "url"))))
      (is (= "url" (:img_url (get-post-by-id "201809201450333"))))
      (is (= 0 (count (get-posts-publish))))
      (is (= 1 (update-post! (assoc post :publish 1))))
      (is (= 1 (count (get-posts-publish))))
      (is (= 1 (delete-post! "201809201450333"))))))


(deftest test-tag
  (testing "load all tags"))

