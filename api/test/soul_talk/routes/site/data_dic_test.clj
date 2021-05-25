(ns soul-talk.routes.site.data-dic-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

(def context "/data-dices")

(deftest get-all
  (testing "get all data-dic"
    (let [resp (h/make-request-by-login-token :get (h/site-uri context "/all"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:data-dices body)))))))

(deftest get-data-dic-by-id
  (testing "view data-dices by id"
    (let [response (h/make-request-by-login-token
                     :get
                     (h/site-uri "/data-dices/" 10))
          body     (h/body response)
          data-dic (:data-dic body)]
      (is (= 200 (:status response)))
      (is (= "10" (:id data-dic))))))

(deftest get-data-dic-by-pic
  (testing "view data-dices by pid"
    (let [response   (h/make-request-by-login-token
                       :get
                       (h/site-uri "/data-dices/pid/" 12))
          body       (h/body response)
          data-dices (:data-dices body)]
      (is (= 200 (:status response)))
      (is (< 0 (count data-dices))))))

(def ^:dynamic *dd* (atom {:id "99"
                           :name "test"
                           :pid "0"}))

(deftest add-data-dic
  (testing "add data-dic"
    (let [resp (h/make-request-by-login-token :post (h/site-uri context) @*dd*)
          body (h/body resp)]
      (is (= 200 (:status resp))))))

(deftest delete-data-dic
  (testing "delete data-dic"
    (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/99"))
          body (h/body resp)]
      (is (= 200 (:status resp))))))
