(ns soul-talk.middleware
  (:require [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [soul-talk.utils :as utils]
            [ring.middleware.flash :refer [wrap-flash]]
            [muuntaja.core :as m]
            [buddy.auth.accessrules :refer [wrap-access-rules restrict]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [taoensso.timbre :as log]
            [soul-talk.env :refer [defaults]]
            [cognitect.transit :as transit]
            [compojure.api.middleware :as cm]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.auth.handler :as auth]
            [compojure.api.exception :as ex]
            [ring.util.http-response :as resp]
            [soul-talk.app-key.handler :as app-key])
  (:import [java.time Instant]))



;; 错误处理
(defn exception-handler [f type]
  (fn [^Exception e data request]
    (utils/log-error e data request type)
    (f (str "发生未知错误,请重试或联系管理员。"))))

;;
(defn request-validation-handler [f type]
  (fn [^Exception e data req]
    (utils/log-error e data req type)
    (let [message (some->> data
                    :problems
                    :clojure.spec.alpha/problems
                    (map :reason)
                    (apply str))]
      (f (if (nil? message) "请求参数错误" message)))))

(defn response-validation-handler [f type]
  (fn [^Exception e data resp]
    (utils/log-error e data resp type)
    (let [message (some->> data
                    :problems
                    :clojure.spec.alpha/problems
                    (map :reason)
                    (apply str))]
      (f (if (nil? message) "响应错误" message)))))


(def exceptions-config
  {:handlers {::calm                   (exception-handler utils/enhance-your-calm :calm)
              java.sql.SQLException    (exception-handler utils/internal-server-error :sql)
              ::ex/request-validation  (request-validation-handler utils/bad-request :request)
              ::ex/request-parsing     (ex/with-logging ex/request-parsing-handler :error)
              ::ex/response-validation (response-validation-handler utils/bad-request :response)
              ::ex/default             (exception-handler utils/internal-server-error :unknown)}})

;; ***** Auth implementation ****************************************************

(defn- parse-header [request token-name]
  (some->> (some-> (resp/find-header request "authorization")
             (second))
    (re-find (re-pattern (str "^" token-name " (.+)$")))
    (second)))

(defn- has-role? [role required-roles]
  (let [has-roles (case role
                    "admin"     #{"any" "user" "poweruser" "admin"}
                    "poweruser" #{"any" "user" "poweruser"}
                    "user"      #{"any" "user"}
                    #{}
                    )
        matched-roles (clojure.set/intersection has-roles required-roles)]
    (not (empty? matched-roles))))

;; 验证和授权 token
(defn wrap-auth [handler roles]
  (fn [request]
    (let [token (some-> (parse-header request "Token")
                        (auth/auth-token)
                        (auth/refresh-token))]
      (if-not token
        (utils/unauthorized)
        (if-not (has-role? "admin" roles)
          (utils/forbidden)
          (handler request))))))


(defn- parse-app-key [request token-name]
  (some->> (some-> (resp/find-header request "X-API-Key")
             (second))
    (re-find (re-pattern (str "^" token-name " (.+)$")))
    (second)))

;; 验证APP key
(defn wrap-app-key [handler rule]
  (fn [request]
    (log/info "starting app-key auth, handler: " handler " rule: " rule)
    (let [app-key (:app-key (:params request))
          app-key (some-> app-key
                    (app-key/auth-app-key))]
      (if-not app-key
        (utils/forbidden "无效的APP KEY")
        (handler request)))))

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
