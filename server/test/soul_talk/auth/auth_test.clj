(ns soul-talk.auth.auth-test
  (:require [clojure.test :refer :all]
            [soul-talk.core-test :refer [api-url parse-body]]
            [soul-talk.handler :refer :all]
            [ring.mock.request :as mock]
            [taoensso.timbre :as log]
            [cheshire.core :as json]))

(def token (atom ""))

(deftest site-routes-test

  (testing "login"
    (test
      (let [user     {:email    "jiesoul@gmail.com"
                      :password "123456789"}
            response (app (-> (mock/request :post "/login")
                            (mock/content-type "application/transit+json")
                            (mock/json-body user)))
            body (parse-body (:body response))]
        (is (= 200 (:status response)))
        (is (= (:data body) user))))

    (test
      (let [user     {:email    "jiesoul@gmail.com"
                      :password "12345678"}
            response (app (-> (mock/request :post "/login")
                            (mock/json-body user)))]
        (is (= 401 (:status response))))))

  (testing "logout"
    (test
      (let [response (app (mock/request :post "/logout"))]
        (log/info "response body: " response)
        (is (= 200 (:status response))))))
  )