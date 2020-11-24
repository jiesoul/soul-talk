(ns soul-talk.series.routes-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

(def context "/series")

(def ^:dynamic *test-series* (atom {:name        (str "test-series" (rand-int 1024))
                                    :description "test series"
                                    :category_id "1201"
                                    :create_by   1
                                    :update_by   1}))

(deftest api-routes-test

  )

(deftest site-routes-test

  (testing "add series"
    (let [series @*test-series*
          resp (h/make-request-by-login-token
                 :post
                 (h/site-uri context "/")
                 series)
          body (h/body resp)
          series (:series body)]
      (is (= 200 (:status resp)))
      (reset! *test-series* series)))


  (testing "view series"
    (let [series @*test-series*
          resp (h/make-request-by-login-token
                 :get
                 (h/site-uri context "/" (:id series)))
          body (h/body resp)]))

  (testing "get all series page"
    (let [resp (h/make-request-by-login-token
                 :get
                 (h/site-uri context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (<= 0 (count (:tags body))))))

  (testing "delete series by id"
    (let [series @*test-series*
          resp (h/make-request-by-login-token
                 :delete
                 (h/site-uri context "/" (:id series)))
          body (h/body resp)]
      (is (= 200 (:status resp)))))
  )