(ns soul-talk.middleware
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.flash :refer [wrap-flash]]
            [muuntaja.middleware :refer [wrap-format]]
            [taoensso.timbre :as log]
            [soul-talk.layout :as layout :refer [*identity* *app-context*]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [soul-talk.env :refer [defaults]])
  (:import (javax.servlet ServletContext)))

(defn wrap-context [handler]
  (fn [request]
    (binding [*app-context*
              (if-let [context (:servlet-context request)]
                (try (.getContextPath ^ServletContext context)
                     (catch IllegalArgumentException _ context))
                "")]
      (handler request))))

;; 内部错误
(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t)
        (layout/error-page {:status 500
                            :title "错误"
                            :message "系统可能发生了一些错误"})))))
;; CSRF
(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (layout/error-page {:status 403
                            :title "无效的 anti-forgery token"})}))

;; session 标识
(defn wrap-identity [handler]
  (fn [request]
    (binding [*identity* (get-in request [:session :identity])]
      (handler request))))

;; 验证和授权
(defn wrap-session-auth [handler]
  (let [backend (session-backend)]
    (-> handler
      wrap-identity
      (wrap-authentication backend)
      (wrap-authorization backend))))

;; 默认
(defn wrap-defaults [handler]
  (ring-defaults/wrap-defaults
    handler
    (-> ring-defaults/site-defaults
      (assoc-in [:security :anti-forgery] false)
      (assoc :session true))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
    wrap-identity
    wrap-webjars
    wrap-flash
    wrap-format
    wrap-defaults
    wrap-internal-error))