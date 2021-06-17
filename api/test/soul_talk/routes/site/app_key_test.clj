(ns soul-talk.routes.site.app-key-test
  (:require [clojure.test :refer :all]
            [soul-talk.handler :refer :all]
            [soul-talk.helper :as h :refer [parse-body]]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]))

(defn context [uri]
  (str "/app-keys" uri))

(def ^:dynamic *token* (atom nil))

(def ^:dynamic *app-key* (atom {:app_name "test-app" :create_by 1 :is_valid 1001 :token (utils/gen-token)}))

(deftest app-key-test

  (testing "gen app key"
    (let [response (h/make-request-by-login-token :get (context "/gen"))
          body     (parse-body (:body response))]
      (is (= 200 (:status response)))
      (is (<= 32 (count (get body :token))))
      (log/debug "gen token: " (:token body))))

  (testing "save app key"
    (let [response (h/make-request-by-login-token :post (context "/") @*app-key*)
          body     (h/body response)
          app-key  (:app-key body)]
      (is (= 200 (:status response)))
      (reset! *app-key* app-key)))

  (testing "load app keys"
    (let [resp (h/make-request-by-login-token
                 :get
                 (context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:app-keys body))))))

  (testing "delete app key"
    (let [app-key  @*app-key*
          _        (log/debug "app-key: " @*app-key*)
          response (h/make-request-by-login-token :delete (context (str "/" (:id app-key))))]
      (is (= 200 (:status response))))))

