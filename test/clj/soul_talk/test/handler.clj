(ns soul-talk.test.handler
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [taoensso.timbre :as log]
            [cheshire.core :as cheshire]))

(def user {:email    "jiesoul@gmail.com"
                  :password "12345678"})

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest test-app

  (testing "home found"
    (let [response (app (mock/request :get "/"))]
      (is (= 200
            (:status response)))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= 404 (:status response)))))

  (testing "load categories"
    (let [response (app (mock/request :get "/api/categories"))]
      (is (= 200 (:status response)))))


  (testing "login"
    (let [response (app (-> (mock/request :post "/api/login")
                          (mock/json-body user)))]
      (log/info response)
      (is (= 200 (:status response)))))

  ;(testing "create category"
  ;  (let [user (app (-> (mock/request :post "/api/login")
  ;                    (mock/json-body @user)))
  ;        response (app (-> (mock/request :post "/api/admin/create-category")
  ;                        (mock/json-body {:category {:name "编程"}})))]
  ;    (is (= 200 (:status response)))))

  )
