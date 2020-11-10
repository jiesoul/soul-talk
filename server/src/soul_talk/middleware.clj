(ns soul-talk.middleware
  (:require [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [soul-talk.utils :as utils]
            [ring.middleware.flash :refer [wrap-flash]]
            [muuntaja.core :as m]
            [soul-talk.auth.handler :refer [auth-backend]]
            [buddy.auth.accessrules :refer [wrap-access-rules]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [taoensso.timbre :as log]
            [soul-talk.env :refer [defaults]]
            [cognitect.transit :as transit]
            [compojure.api.middleware :as cm]
            [compojure.api.exception :as ex]
            [ring.util.http-response :as resp])
  (:import [java.time Instant]))

;; 错误处理
(defn exception-handler [f type]
  (fn [^Exception e data request]
    (log/error "exception -- " e)
    (f {:result :error :message (str "发生未知错误"), :type type})))

;;
(defn request-validation-handler [f type]
  (fn [^Exception e data req]
    (log/error " 请求发生错误：" (.getMessage e) "\n")
    (let [message (->> data
                    :problems
                    :clojure.spec.alpha/problems
                    (map :reason))]
      (log/info "错误信息：" message)
      (f {:result :error :message message}))))

(defn response-validation-handler [f type]
  (fn [^Exception e data resp]
    (log/error " 响应发生错误：" (.getMessage e))
    (let [message (->> data
                    :problems
                    :clojure.spec.alpha/problems)]
      (f {:result :error :message message}))))

(def exceptions-config
  {:handlers {::calm                   (exception-handler resp/enhance-your-calm :calm)
              java.sql.SQLException    (exception-handler resp/internal-server-error :sql)
              ::ex/request-validation  (request-validation-handler resp/bad-request :error)
              ::ex/request-parsing     (ex/with-logging ex/request-parsing-handler :error)
              ::ex/response-validation (response-validation-handler resp/internal-server-error :error)
              ::ex/default             (exception-handler resp/internal-server-error :unknown)}})

;; ***** Auth implementation ****************************************************

;; 验证和授权 token
(defn wrap-auth [handler rule]
  (-> handler
    (wrap-authentication rule)
    (wrap-authorization rule)))

;; 内部错误
(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error "发生内部错误：" t)
        (utils/internal-server-error)))))
;;
(defn wrap-defaults [handler]
  (ring-defaults/wrap-defaults
    handler
    (-> ring-defaults/api-defaults
      (assoc-in [:security :anti-forgery] false)
      (assoc :session true))))

;; custom Record
(defrecord Ping [])

;; custom transit handlers
(def write-handlers
  {Ping (transit/write-handler (constantly "Ping") (constantly {}))})

(def read-handlers
  {"Ping" (transit/read-handler map->Ping)})

;; a configured Muuntaja
(def muuntaja
  (m/create
    (update-in
      m/default-options
      [:formats "application/transit+json"]
      merge
      {:encoder-opts {:handlers write-handlers}
       :decoder-opts {:handlers read-handlers}})))

(def format-options
  (-> m/default-options
    (update-in [:formats "application/transit+json" :encode-opts]
      (partial merge {:handlers
                      {Instant (transit/write-handler (constantly "Instant")
                                 #(.toString %))}}))
    (update-in [:formats "application/transit+json" :encode-opts]
      (partial merge {:handlers
                      {"Instant" (transit/read-handler #(Instant/parse %))}}))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
    (wrap-cors :access-control-allow-origin [#".*"]
      :access-control-allow-methods [:get :post :put :delete :options])
    cm/wrap-format
    wrap-flash
    wrap-multipart-params
    wrap-defaults
    wrap-internal-error
    ))
