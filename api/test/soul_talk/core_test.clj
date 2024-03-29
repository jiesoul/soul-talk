(ns soul-talk.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.helper :as h]))

(deftest default-test ^:test-refresh/focus
  (testing "api "
    (let [response (app (-> (mock/request :get "/v1/api-docs")))]
      (is (= (:status response) 302)))))