(ns soul-talk.user.user-routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [taoensso.timbre :as log]
            [cheshire.core :as cheshire]))

(def api-pre "http://localhost:3000/api/v1")

(defn api-url [url]
  (str api-pre url))

(def user {:email    "jiesoul@gmail.com"
                  :password "12345678"})

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest user-test

  (testing "logout"
    (test
      (let [response (app (mock/request :post (api-url "/logout")))]
        (log/info "response body: " response)
        (is (= 200 (:status response))))))


  (testing "login"
    (test
      (let [user     {:email    "jiesoul@gmail.com"
                      :password "123456789"}
            response (app (-> (mock/request :post (api-url "/login"))
                            (mock/json-body user)))]
        (is (= 200 (:status response)))))

    (test
      (let [user     {:email    "jiesoul@gmail.com"
                      :password "12345678"}
            response (app (-> (mock/request :post (api-url "/login"))
                            (mock/json-body user)))]
        (is (= 401 (:status response))))))

  (testing "register user"
    (test
      (let [user {:email "jiesoul@email.com"
                  :username "jiesoul"
                  :password "12345678"}
            response (app (-> (mock/request :post (api-url "/register"))))]
        (is (= 400 (:status response))))))

    (testing "get user profile"
      (let [response (app (-> (mock/request :get (api-url "/users/1/profile"))))]
        (test
          (is (= 200 (:status response))))))
  )
