(ns soul-talk.helper
  (:require [clojure.test :refer :all]
            [soul-talk.handler :refer :all]
            [cheshire.core :as cheshire]
            [ring.mock.request :as mock]
            [soul-talk.utils :as utils]
            [clojure.tools.logging :as log]
            [clojure.string :as s]))

(defn start-states [f]
  (mount.core/start)
  (f))

(use-fixtures :once start-states)

(defn api-url [context & url ]
  (apply str "/api/v1" context url))

(defn site-uri [context & url]
  (apply str context url))

(def ^:dynamic *login-token* (atom nil))
(def ^:dynamic *app-token*  (atom (str "pmyzXOP27cbvyyqDuEWGM1WAy4Bw1UKK_qpYzfP63rk")))

(def user {:email    "jiesoul@gmail.com"
           :password "12345678"})

(defn parse-body [body]
  (cheshire/parse-string (slurp body) true))

(defn body [response]
  (if (= 200 (:status response))
    (parse-body (:body response))
    nil))

(defn get-login-token! []
  (let [response    (app (-> (mock/request :post "/login")
                           (mock/content-type "application/json")
                           (mock/json-body user)))
        body        (parse-body (:body response))
        login-token (str "Token " (get body :token))]
    (log/info "===========" body)
    (reset! *login-token* login-token)
    login-token))

(defn login-token!
  []
  (if @*login-token*
    @*login-token*
    (get-login-token!)))

(defn get-app-key! []
  (if @*app-token*
    @*app-token*
    (let [token     (utils/gen-token)
          response  (app (-> (mock/request :post "/app-keys")
                           (mock/content-type "application/json")
                           (mock/json-body {:app_name  "web"
                                            :token     token
                                            :create_by 1})))
          body      (parse-body (:body response))
          app-token (str "AppKey " (get-in body [:app-key :token]))]
      (reset! @*app-token* app-token)
      (log/info "setting app token to: " app-token)
      app-token)))

(defn make-header [request header]
  (if (empty? header)
    request
    (let [[k v] (first header)
          request (mock/header request k v)]
      (make-header request (rest header)))))

(defn make-request [method uri header body]
  (let [response (app (-> (mock/request method uri)
                        (mock/content-type "application/json")
                        (make-header header)
                        (mock/json-body body)))]
    (log/info "response: " response)
    (log/info "response body: " (body response))
    response))

(defn make-request-by-login-token
  ([method uri] (make-request-by-login-token method uri {}))
  ([method uri body]
   (let [login-token (login-token!)
         header {:Authorization login-token}]
     (make-request method uri header body))))

(defn make-request-by-app-token
  ([method uri] (make-request-by-app-token method uri {}))
  ([method uri body]
   (let [uri (str uri (if (s/includes? uri "?") "&" "?")  "app-key=" @*app-token*)]
     (make-request method uri {} body))))