(ns soul-talk.user.user-routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.helper :refer [user parse-body]]
            [soul-talk.helper :as h]
            [taoensso.timbre :as log]))


(deftest user-api-test

  (testing "get user profile"
    (let [response (h/make-request-by-app-token :get "/api/v1/users/1/profile")]
      (is (= 200 (:status response)))))
  )

(deftest user-site-test

  (testing "first login get token"

    (testing "get users all"
      (let [response (h/make-request-by-login-token :get
                       "/users")]
        (is (= 200 (:status response)))))

    (testing "get users by like name"
      (let [response (h/make-request-by-login-token :get
                       "/users?name=sss")
            body     (parse-body (:body response))]
        (test
          (is (= 200 (:status response))))))

    (testing "update profile ok"
      (let [response (h/make-request-by-login-token :patch
                       "/users/1/profile"
                       {:username "test"})]
        (test
          (is (= 200 (:status response))))))


    (testing "update password"
      (let [response (h/make-request-by-login-token :patch
                       "/users/1/password"
                       {:oldPassword     "12345678"
                        :newPassword     "123456789"
                        :confirmPassword "123456789"})]
        (test
          (is (= 200 (:status response))))))


    (testing "update password"
      (let [response (h/make-request-by-login-token :patch
                       "/users/1/password"
                       {:oldPassword     "123456789"
                        :newPassword     "12345678"
                        :confirmPassword "12345678"})
            body (h/body response)]
        (log/info "response: " body)
        (test
          (is (= 200 (:status response))))))

    ))
