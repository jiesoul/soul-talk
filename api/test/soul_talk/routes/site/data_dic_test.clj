(ns soul-talk.routes.site.data-dic-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

(deftest site-routes-test
  (testing "view data-dices by id"
    (let [response (h/make-request-by-app-token
                     :get
                     (h/api-url "/data-dices/" 10))
          body (h/body response)
          data-dic (:data-dic body)]
      (is (= 200 (:status response)))
      (is (= "10" (:id data-dic))))
    )
  (testing "view data-dices by pid"
    (let [response (h/make-request-by-app-token
                     :get
                     (h/api-url "/data-dices/pid/" 12))
          body (h/body response)
          data-dices (:data-dices body)]
      (is (= 200 (:status response)))
      (is (< 0 (count data-dices)))))
  )

(deftest api-routes-test
  (testing "add a data-dic"
    (let [data-dic {:id "9999"
                    :name "test"
                    :pid "99"}
          response (h/make-request-by-login-token
                     :post
                     (h/site-uri "/data-dices")
                     data-dic)
          body (h/body response)]
      (is (= 200 (:status response)))))

  (testing "delete a data-dic"
    (let [resp (h/make-request-by-login-token
               :delete
               (h/site-uri "/data-dices/9999"))
          body (h/body resp)]
      (is (= 200 (:status resp))))))
