(ns soul-talk.routes.site.app-key-test
  (:require [clojure.test :refer :all]
            [soul-talk.handler :refer :all]
            [soul-talk.helper :as h :refer [parse-body]]))

(defn context [uri]
  (str "/app-keys" uri))

(def ^:dynamic *token* (atom nil))

(def ^:dynamic *app-key* (atom nil))

(deftest gen-app-key
  (testing "gen app key"
    (let [response (h/make-request-by-login-token :get (context "/gen"))
          body     (parse-body (:body response))]
      (is (= 200 (:status response)))
      (is (< 32 (count (get body :token))))
      (reset! *token* (:token body)))))

(deftest sava-app-key
  (testing "save app key"
    (let [app-key @*app-key*
          response (h/make-request-by-login-token :post (context "/")
                     (assoc app-key :token @*token*))
          body     (parse-body (:body response))
          app-key  (get body :app-key)]
      (is (= 200 (:status response)))
      (reset! *app-key* app-key))))

(deftest load-app-keys
  (testing "load app keys"
    (let [resp (h/make-request-by-login-token
                 :get
                 (context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:app-keys body)))))))

(deftest delete-app-key
  (testing "delete app key"
    (let [app-key @*app-key*
          response (h/make-request-by-login-token :delete (context (str "/" (:id app-key))))]
      (is (= 200 (:status response))))))