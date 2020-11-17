(ns soul-talk.app-key.routes-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [soul-talk.helper :as h :refer [parse-body]]
            [taoensso.timbre :as log]
            [soul-talk.utils :as utils]))

(defn context [uri]
  (str "/app-keys" uri))


(deftest site-routes-test
  (let [app-key {}]
    (testing "gen app key"
      (let [response (h/make-request-by-login-token :get (context "/gen"))
            body     (parse-body (:body response))]
        (test
          (is (= 200 (:status response))))
        (test
          (is (= 44 (count (get body :token)))))))

    (testing "save app key"
      (let [token (utils/gen-token)
            response (h/make-request-by-login-token :post (context "/")
                       {:app_name  "web"
                        :token     token
                        :create_by 1})
            body     (parse-body (:body response))
            app-key (get body :app-key)]
        (test (is (= 200 (:status response)))))
      )

    (testing "delete app key"
      (let [response (h/make-request-by-login-token :delete (context (str "/1")))
            body (parse-body (:body response))]
        (log/info app-key)))
    )
  )