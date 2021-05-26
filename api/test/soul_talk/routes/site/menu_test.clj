(ns soul-talk.routes.site.menu-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

(def context "/menus")

(def m1 {:id 99 :name "测试" :pid 0 :create_by 1})
(def m2 {:id 9901 :name "测试1" :pid 99 :create_by 1})

(deftest add-menu
  (testing "add menu {:id   \"99\" :name \"测试\" :pid  \"0\"}"
    (let [resp (h/make-request-by-login-token :post (h/site-uri context) m1)
          body (h/body resp)]
      (is (= 200 (:status resp)))))
  (testing "add menu {:id \"9901\" :name \"测试1\" :pid \"99\"}"
    (let [resp (h/make-request-by-login-token :post (h/site-uri context) m2)
          body (h/body resp)]
      (is (= 200 (:status resp))))))

(deftest get-menu-by-id
  (testing "view menu by id"
    (let [response (h/make-request-by-login-token
                     :get
                     (h/site-uri context "/" 11))
          body     (h/body response)
          menu (:menu body)]
      (is (= 200 (:status response)))
      (is (= 11 (:id menu))))))

(deftest get-menu-by-ids
  (testing "view menus by ids"
    (let [response   (h/make-request-by-login-token
                       :get
                       (h/site-uri context "?ids=11,12,13"))
          body       (h/body response)
          menus (:menus body)]
      (is (= 200 (:status response)))
      (is (< 0 (count menus))))))

(deftest get-menu-by-pid
  (testing "view menus by pid"
    (let [response   (h/make-request-by-login-token
                       :get
                       (h/site-uri context "/pid/" 11))
          body       (h/body response)
          menus (:menus body)]
      (is (= 200 (:status response)))
      (is (< 0 (count menus))))))

(deftest delete-menu
  ;(testing "delete menu {:id   \"99\" :name \"测试\" :pid  \"0\"} error reason: child"
  ;  (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/99"))
  ;        body (h/body resp)]
  ;    (is (= 400 (:status resp)))))

  (testing "delete menu {:id \"9901\" :name \"测试1\" :pid \"99\"} ok"
    (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/9901"))
          body (h/body resp)]
      (is (= 200 (:status resp)))))

  (testing "delete menu {:id   \"99\" :name \"测试\" :pid  \"0\"} ok"
    (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/99"))
          body (h/body resp)]
      (is (= 200 (:status resp)))))
  )
