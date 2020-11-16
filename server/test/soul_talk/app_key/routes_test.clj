(ns soul-talk.app-key.routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.core-test :refer [parse-body get-login-token!]]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]))

(defn context [uri]
  (str "/app-keys" uri))
(def login-token (get-login-token!))
(def app-token (utils/gen-token))

(deftest site-routes-test
  (let [app-key {}]
    (testing "gen app key"
      (let [response (app (-> (mock/request :get (context "/gen"))
                            (mock/content-type "application/json")
                            (mock/header :Authorization login-token)))
            body     (parse-body (:body response))]
        (test
          (is (= 200 (:status response))))
        (test
          (is (= 44 (count (get-in body [:data :token])))))))

    (testing "save app key"
      (let [response (app (-> (mock/request :post (context "/"))
                            (mock/content-type "application/json")
                            (mock/header :Authorization login-token)
                            (mock/json-body {:app_name  "web"
                                             :token     app-token
                                             :create_by 1})))
            body     (parse-body (:body response))
            app-key (get-in body [:data :app-key])]
        (test (is (= 200 (:status response)))))
      )

    (testing "delete app key"
      (let [response (app (-> (mock/request :delete (context (str "/1")))
                            (mock/content-type "application/json")
                            (mock/header :Authorization login-token)))
            body (parse-body (:body response))]
        (log/info app-key)))
    )
  )