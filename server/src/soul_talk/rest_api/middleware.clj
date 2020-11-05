(ns soul-talk.rest-api.middleware
  (:require [clojure.string :as str]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [ring.middleware.flash :refer [wrap-flash]]
            [muuntaja.middleware :refer [wrap-format]]
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
            [muuntaja.core :as m]
            [ring.util.http-response :as resp]
            [soul-talk.utils :refer [parse-int]])
  (:import [java.time Instant]))

(defn- parse-header [request token-name]
  (some->> (some->
             (resp/find-header request "authorization")
             (second))
    (re-find (re-pattern (str "^" token-name " (.+$")))
    (second)))

(defn- parse-token [token]
  )

(defn- not-expire? [user]
  (let [now (quot (System/currentTimeMillis) 1000)]
    (if (< now (-> user :exp parse-int))
      user
      nil)))

(defn- has-role? [role required-roles]
  (let [has-roles (case role
                    "admin" #{"any" "user" "poweruser" "admin"}
                    "poweruser" #{"any" "user" "poweruser"}
                    "user" #{"any" "user"}
                    #{})
        matched-roles (clojure.set/intersection has-roles required-roles)]
    (not (empty? matched-roles))))

(defn wrap-require-roles [handler roles]
  (fn [request]
    (let [user (some->
                 (parse-header request "Token")
                 (parse-token)
                 (not-expire?))]
      (if-not user
        (resp/unauthorized "用户未验证")
        (if-not (has-role? (:role user) roles)
          (resp/forbidden "权限不足")
          (let [request (assoc request :identity user)]
            (handler request)))))))

(defn error-500 []
  {:status 500
   :body   {:result :error
            :message "内部错误"}})

(defn error-403 []
  {:status 403
   :body {:result  :error
          :message "无效的 anti token"}})

(defn error-401 [req val]
  {:status  401
   :headers {}
   :body    {:result  :error
             :message "未认证或认证过期，请重新登录或者联系管理员."}})

;; 内部错误
(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error "发生内部错误：" t)
        (error-500)))))

;; 验证和授权 token
(defn wrap-token-auth
  [handler]
  (-> handler
    (wrap-authentication auth-backend)
    (wrap-authorization auth-backend)))

(defn wrap-rule [handler rule]
  (-> handler
    (wrap-access-rules {:rules [{:pattern #".*"
                                 :handler rule}]
                        :on-error error-401})))

(defn wrap-auth-api-key [handler rule]
  (-> handler
    (wrap-access-rules {:rules [{:pattern #".*"
                                 :handler rule}]
                        :on-error error-401})))
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
    wrap-token-auth
    wrap-flash
    ;(wrap-format format-options)
    wrap-multipart-params
    wrap-defaults
    wrap-internal-error
    ))
