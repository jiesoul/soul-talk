(ns soul-talk.middleware
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
            [muuntaja.middleware :refer [wrap-format]]
            [taoensso.timbre :as log]
            [soul-talk.layout :as layout :refer [*identity*]]))

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t)
        (layout/error-page {:status 500
                            :title "错误发生了"
                            :message "管理员会尽快处理"})))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     (layout/error-page {:status 403
                         :title "无效的 anti-forgery token"})}))

(defn on-error [request response]
  (layout/error-page {:status 403
                      :title (str "访问 " (:uri request) " 需要验证")}))

(defn wrap-identity [handler]
  (fn [request]
    (binding [*identity* (get-in request [:session :identity])]
      (handler request))))

(defn wrap-base [handler]
  (-> handler
      (wrap-identity)
      (wrap-webjars)
      (wrap-session)
      (wrap-format)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-internal-error)))