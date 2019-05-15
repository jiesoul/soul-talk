(ns soul-talk.dev-middleware
  (:require [ring.middleware.reload :refer [wrap-reload]]
            [selmer.middleware :refer [wrap-error-page]]))


(defn wrap-dev [handler]
  (-> handler
    wrap-reload
    wrap-error-page))