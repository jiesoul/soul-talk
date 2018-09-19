(ns soul-talk.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [taoensso.timbre :as log]))

(deftest test-app

  (testing "home found"
    (let [response (app (mock/request :get "/"))]
      (is (= 200
            (:status response)))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= 404 (:status response))))))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 0))))

(deftest b-test
  (testing "Test i success"
    (is (= 1 1))))
