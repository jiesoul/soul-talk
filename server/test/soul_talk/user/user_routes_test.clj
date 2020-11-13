(ns soul-talk.user.user-routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.core-test :refer [api-url]]))

(deftest user-test

    (testing "get user profile"
      (let [response (app (-> (mock/request :get "/api/v1/users/1/profile")))]
        (test
          (is (= 403 (:status response))))))
  )
