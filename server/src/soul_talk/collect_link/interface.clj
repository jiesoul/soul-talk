(ns soul-talk.collect-link.interface
  (:require [soul-talk.collect-link.handler :as handler]
            [soul-talk.collect-link.spec :as spec]))
(def token spec/token)
(def create-collect-link spec/create-collect-link)

(defn auth-collect-link [token]
  (handler/auth-collect-link token))

(defn gen-collect-link []
  (handler/gen-collect-link))

(defn save-collect-link [collect-link]
  (handler/save-collect-link collect-link))

(defn delete-collect-link [id]
  (handler/delete-collect-link id))

(defn load-collect-link [req]
  (handler/load-collect-links req))

(defn query-collect-link [req]
  (handler/query-collect-link req))
