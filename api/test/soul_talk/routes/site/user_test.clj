(ns soul-talk.routes.site.user-test
  (:require [clojure.test :refer :all]
            [soul-talk.handler :refer :all]
            [soul-talk.helper :refer [user parse-body]]
            [soul-talk.helper :as h]
            [clojure.tools.logging :as log]))


;(deftest user-api-test
;
;  (testing "get user profile"
;    (let [response (h/make-request-by-app-token :get "/v1/users/1/profile")]
;      (is (= 200 (:status response)))))
;  )

(deftest get-all-users

  (testing "get users all"
    (let [response (h/make-request-by-login-token :get
                     "/users")]
      (is (= 200 (:status response)))))

  (testing "get users by like name"
    (let [response (h/make-request-by-login-token :get
                     "/users?name=sss")
          body     (parse-body (:body response))]
      (test
        (is (= 200 (:status response)))))))

(def user-id 1)

(deftest load-user
  (testing "load user by id"
    (let [response (h/make-request-by-login-token :get (str "/users/" user-id))
          body (h/body response)]
      (test (is (= 200 (:status response))))
      (test (is (= user-id (:id (:user body))))))))

(deftest update-user
  (testing "update user profile"
    (let [name "test"
          resp (h/make-request-by-login-token :patch (str "/users/" user-id) {:id user-id
                                                                              :name name})
          body (h/body resp)]
      (test (is (= 200 (:status resp)))))))

(deftest load-user-roles
  (testing "load user roles"
    (let [resp (h/make-request-by-login-token :get (str "/users/" user-id "/roles"))
          body (h/body resp)]
      (test (is (= 200 (:status resp)))))))

(deftest load-user-auth-keys
  (testing "load user auth keys page"
    (let [resp (h/make-request-by-login-token :get (str "/users/auth-keys"))
          body (h/body resp)]
      (test (is (= 200 (:status resp)))))))

;(deftest update-password
;  (testing "update password"
;    (let [response (h/make-request-by-login-token :patch
;                     "/users/1/password"
;                     {:old-password     "12345678"
;                      :new-password     "123456789"
;                      :confirm-password "123456789"})]
;      (test
;        (is (= 200 (:status response))))))
;
;
;  (testing "update password"
;    (let [response (h/make-request-by-login-token :patch
;                     "/users/1/password"
;                     {:old-password     "123456789"
;                      :new-password     "12345678"
;                      :confirm-password "12345678"})
;          body (h/body response)]
;      (test
;        (is (= 200 (:status response)))))))
;
;(deftest update-profile
;  (testing "update profile"
;    (let [response (h/make-request-by-login-token :patch
;                     "/users/1/profile"
;                     {:username "test"})]
;      (test
;        (is (= 200 (:status response)))))))
