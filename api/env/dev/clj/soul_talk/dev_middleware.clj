(ns soul-talk.dev-middleware
  (:require [ring.middleware.reload :refer [wrap-reload]]))


(defn wrap-dev [handler]
  (-> handler
    wrap-reload))