(ns soul-talk.core-test
  (:require [clojure.test :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [soul-talk.handler :refer :all]
            [taoensso.timbre :as log]))

(defn start-states [f]
  (mount.core/start)
  (f))

(use-fixtures :once start-states)

(defn api-url [url]
  (str "/api/v1" url))

(def user {:email    "jiesoul@gmail.com"
           :password "12345678"})

(def ^:dynamic *token* (atom ""))

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(deftest default-test
  (testing "api "
    (let [response (app (-> (mock/request :get "/api-docs")))]
      (log/debug "default test response message:" response)
      (is (= (:status response) 302)))))