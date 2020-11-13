(ns soul-talk.auth.auth-test
  (:require [clojure.test :refer :all]
            [soul-talk.core-test :refer [api-url user parse-body *token*]]
            [soul-talk.handler :refer :all]
            [ring.mock.request :as mock]
            [taoensso.timbre :as log]))

(deftest auth-routes-test

  (testing "login"
    (test
      (let [response (app (-> (mock/request :post "/login")
                            (mock/content-type "application/json")
                            (mock/json-body user)))]
        (log/info "response: " response)
        (is (= 200 (:status response))))))

  (testing "logout"
    (test
      (let [req {:user_id 1}
            response (app (-> (mock/request :post "/logout")
                            (mock/content-type "application/json")
                            (mock/json-body req)))]
        (log/info "response body: " response)
        (is (= 200 (:status response))))))
  )