(ns soul-talk.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [taoensso.timbre :as log]))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest default-test
  (testing "api "
    (let [response (app (-> (mock/request :get "/api-docs")))]
      (log/debug "default test response message:" response)
      (is (= (:status response) 302))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= 404 (:status response))))))
