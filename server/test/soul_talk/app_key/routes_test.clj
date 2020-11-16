(ns soul-talk.app-key.routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.core-test :refer [parse-body *token*]]
            [soul-talk.auth.auth-test :refer [login-test]]))

(login-test)

(deftest "site-routes-test"


  (testing "gen key"
    (let [response (app (-> (mock/request :get "/app-keys/gen")
                          (mock/header :Authorization @*token*)))
          body     (parse-body (:body response))]
      (test
        (is (= 200 (:status response))))))
  )