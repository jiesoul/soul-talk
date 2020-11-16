(ns soul-talk.user.user-routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.core-test :refer [api-url app-key user parse-body]]
            [taoensso.timbre :as log]
            [cheshire.core :as json]))


(deftest user-api-test

  (testing "get user profile"
    (let [response (app (-> (mock/request :get "/api/v1/users/1/profile")
                          (mock/header :Authorization app-key)))]
      (test
        (is (= 200 (:status response))))))
  )

(deftest user-site-test

  (testing "first login get token"

    (let [response (app (-> (mock/request :post "/login")
                          (mock/json-body user)))
          body     (parse-body (:body response))
          token    (str "Token " (get-in body [:data :token]))]

      (testing "get users all"
        (let [response (app (-> (mock/request :get "/users")
                              (mock/header :Authorization token)))]
          (test
            (is (= 200 (:status response))))))

      (testing "get users by like name"
        (let [response (app (-> (mock/request :get "/users?name=sss")
                              (mock/header :Authorization token)))
              body (parse-body (:body response))]
          (test
            (is (= 200 (:status response))))))

      (testing "update profile ok"
        (let [response (app (-> (mock/request :patch "/users/1/profile")
                              (mock/header :Authorization token)
                              (mock/json-body {:username "test"})))]
          (test
            (is (= 200 (:status response))))))

      ;(testing "update profile error"
      ;  (let [response (app (-> (mock/request :patch "/users/1/profile")
      ;                        (mock/header :Authorization token)
      ;                        (mock/json-body {:name "test"})))]
      ;    (test
      ;      (is (= 400 (:status response))))))


      (testing "update password"
        (let [response (app (-> (mock/request :patch "/users/1/password")
                              (mock/header :Authorization token)
                              (mock/json-body {:oldPassword "12345678"
                                               :newPassword "123456789"
                                               :confirmPassword "123456789"})))]
          (test
            (is (= 200 (:status response))))))


      (testing "update password"
        (let [response (app (-> (mock/request :patch "/users/1/password")
                              (mock/header :Authorization token)
                              (mock/json-body {:oldPassword "123456789"
                                               :newPassword "12345678"
                                               :confirmPassword "12345678"})))]
          (test
            (is (= 200 (:status response))))))


      )))
