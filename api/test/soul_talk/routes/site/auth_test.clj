(ns soul-talk.routes.site.auth-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [soul-talk.handler :refer :all]
            [ring.mock.request :as mock]
            [clojure.tools.logging :as log]))

(deftest login-test
  (testing "login"
    (test
      (let [response (app (-> (mock/request :post "/login")
                            (mock/content-type "application/json")
                            (mock/json-body h/user)))
            body (h/body response)]
        (is (= 200 (:status response)))
        (is (string? (:token (:user body))))))))

(deftest logout-test

  (testing "logout"
    (test
      (let [req {:user_id 1}
            response (app (-> (mock/request :post "/logout")
                            (mock/content-type "application/json")
                            (mock/json-body req)))]
        (is (= 200 (:status response))))))
  )

