(ns soul-talk.helper
  (:require [clojure.test :refer :all]
            [soul-talk.handler :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]))

(defn start-states [f]
  (mount.core/start)
  (f))

(use-fixtures :once start-states)

(defn api-url [context url & opt]
  (apply str "/api/v1" context url opt))

(defn site-uri [context url & opt]
  (apply str context url opt))

(def ^:dynamic *token* nil)
(def gen-app-token (utils/gen-token))
(def app-token "AppKey ty57zxCEOc6KQeEcAz6PZcb3FneD2p7ANrsm0rmZID4=")

(def user {:email    "jiesoul@gmail.com"
           :password "12345678"})

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(defn body [response]
  (if (= 200 (:status response))
    (parse-body (:body response))
    nil))

(defn get-login-token! []
  (let [response (app (-> (mock/request :post "/login")
                        (mock/content-type "application/json")
                        (mock/json-body user)))
        body (parse-body (:body response))]
    (str "Token " (get body :token))))

(def login-token (get-login-token!))

(defn make-request-by-login-token
  ([method uri] (make-request-by-login-token method uri {}))
  ([method uri body]
   (let [response (app (-> (mock/request method uri)
                         (mock/content-type "application/json")
                         (mock/header :Authorization login-token)
                         (mock/json-body body)))]
     response)))

(defn make-request-by-app-token
  ([method uri] (make-request-by-app-token method uri {}))
  ([method uri body]
   (let [response (app (-> (mock/request method uri)
                         (mock/content-type "application/json")
                         (mock/header :Authorization app-token)
                         (mock/json-body body)))]
     response)))