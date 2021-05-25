(ns soul-talk.routes.api.data-dic-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

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
