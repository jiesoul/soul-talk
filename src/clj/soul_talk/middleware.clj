(ns soul-talk.middleware
  (:require [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]))

(defn wrap-nocache [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-base [handler]
  (-> handler
      (wrap-nocache)
      (wrap-reload)
      (wrap-webjars)
      (wrap-restful-format :formats [:json-kw])
      (wrap-session)
      (wrap-defaults (assoc-in api-defaults [:security :anti-forgery] true))))