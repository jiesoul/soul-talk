(ns soul-talk.auth.auth-test
  (:require [clojure.test :refer :all]
            [soul-talk.core-test :refer [api-url user parse-body *token*]]
            [soul-talk.handler :refer :all]
            [ring.mock.request :as mock]
            [taoensso.timbre :as log]))

(deftest login-test
  (testing "login"
    (test
      (let [response (app (-> (mock/request :post "/login")
                            (mock/content-type "application/json")
                            (mock/json-body user)))
            body (parse-body (:body response))]
        (is (= 200 (:status response)))
        (swap! *token* (str "Token " (get-in body [:data :token])))
        ))))

(deftest logout-test

  (testing "logout"
    (test
      (let [req {:user_id 1}
            response (app (-> (mock/request :post "/logout")
                            (mock/content-type "application/json")
                            (mock/json-body req)))]
        (is (= 200 (:status response))))))
  )