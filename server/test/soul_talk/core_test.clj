(ns soul-talk.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [taoensso.timbre :as log]
            [mount.core :as mount]))



(defn start-states [f]
  (mount.core/start)
  (f))

(use-fixtures :once start-states)

(defn api-url [url]
  (str "/api/v1" url))

(def ^:dynamic *token* nil)
(def app-key "AppKey ty57zxCEOc6KQeEcAz6PZcb3FneD2p7ANrsm0rmZID4=")

(def user {:email    "jiesoul@gmail.com"
           :password "12345678"})

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))



(deftest default-test
  (testing "api "
    (let [response (app (-> (mock/request :get "/api/v1/api-docs")))]
      (is (= (:status response) 302)))))

(defn get-login-token! []
  (let [response (app (-> (mock/request :post "/login")
                        (mock/content-type "application/json")
                        (mock/json-body user)))
        body (parse-body (:body response))]
    (str "Token " (get-in body [:data :token]))))

(defn make-request-token
  []
  (let [header (format "Token %s" (get-login-token!))]
    {:header {:Authorization header}}))