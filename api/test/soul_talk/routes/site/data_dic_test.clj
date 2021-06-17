(ns soul-talk.routes.site.data-dic-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

(def context "/data-dices")
(def dd {:id "99" :name "测试" :pid "0"})
(def dd1 {:id "9901" :name "测试1" :pid "99"})

(deftest data-dic-test

  (testing "get all data-dic"
    (let [resp (h/make-request-by-login-token :get (h/site-uri context "/all"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:data-dices body))))))

  (testing "view data-dices by id"
    (let [response (h/make-request-by-login-token
                     :get
                     (h/site-uri "/data-dices/" 10))
          body     (h/body response)
          data-dic (:data-dic body)]
      (is (= 200 (:status response)))
      (is (= "10" (:id data-dic)))))

  (testing "view data-dices by pid"
    (let [response   (h/make-request-by-login-token
                       :get
                       (h/site-uri "/data-dices/pid/" 12))
          body       (h/body response)
          data-dices (:data-dices body)]
      (is (= 200 (:status response)))
      (is (< 0 (count data-dices)))))

  (testing "add-data-dic"
    (testing "add data-dic {:id   \"99\" :name \"测试\" :pid  \"0\"}"
      (let [resp (h/make-request-by-login-token :post (h/site-uri context) dd)
            body (h/body resp)]
        (is (= 200 (:status resp)))))
    (testing "add data-dic {:id \"9901\" :name \"测试1\" :pid \"99\"}"
      (let [resp (h/make-request-by-login-token :post (h/site-uri context) dd1)
            body (h/body resp)]
        (is (= 200 (:status resp))))))

  (testing "update data-dic {:id 99 } to {:name \"测试更新\"}"
    (let [resp (h/make-request-by-login-token :patch (h/site-uri context) (update dd :name "测试更新"))
          body (h/body resp)]
      (is (= 200 (:status resp)))))

  (testing "delete-data-dic"
    (testing "delete data-dic {:id   \"99\" :name \"测试\" :pid  \"0\"} error reason: child"
      (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/99"))
            body (h/body resp)]
        (is (= 400 (:status resp)))))
    (testing "delete data-dic {:id \"9901\" :name \"测试1\" :pid \"99\"} ok"
      (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/9901"))
            body (h/body resp)]
        (is (= 200 (:status resp)))))

    (testing "delete data-dic {:id   \"99\" :name \"测试\" :pid  \"0\"} ok"
      (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/99"))
            body (h/body resp)]
        (is (= 200 (:status resp)))))))
