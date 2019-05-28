(ns soul-talk.middleware
  (:require [ring.middleware.defaults :as ring-defaults]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.flash :refer [wrap-flash]]
            [muuntaja.middleware :refer [wrap-format]]
            [taoensso.timbre :as log]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [soul-talk.models.auth-model :refer [auth-backend]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [soul-talk.env :refer [defaults]])
  (:import (javax.servlet ServletContext)))

(declare ^:dynamic *identity*)
(declare ^:dynamic *app-context*)

(defn error-500 []
  {:status 500
   :body   {:result :error
            :message "内部错误"}})

(defn error-403 []
  {:status 403
   :body {:result  :error
          :message "无效的 anti token"}})

(defn error-401 []
  {:status  401
   :headers {}
   :body    {:result  :error
             :message "Unauthorized"}})

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
        (error-500)))))
;; CSRF
(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response error-403}))

;; session 标识
(defn wrap-identity [handler]
  (fn [request]
    (binding [*identity* (get-in request [:session :identity])]
      (handler request))))

;; 验证和授权 session
(defn wrap-session-auth [handler]
  (let [backend (session-backend)]
    (-> handler
      wrap-identity
      (wrap-authentication backend)
      (wrap-authorization backend))))

;; 验证和授权 token
(defn wrap-token-auth
  [handler]
  (-> handler
    (wrap-authentication auth-backend)
    (wrap-authorization auth-backend)))

;; 默认
(defn wrap-defaults [handler]
  (ring-defaults/wrap-defaults
    handler
    (-> ring-defaults/api-defaults
      (assoc-in [:security :anti-forgery] false)
      (assoc :session true))))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
    (wrap-cors :access-control-allow-origin [#".*"]
                :access-control-allow-methods [:get :post :put :delete])
    wrap-identity
    wrap-token-auth
    wrap-flash
    wrap-format
    wrap-multipart-params
    wrap-defaults
    wrap-internal-error
    ))