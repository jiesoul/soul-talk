(ns soul-talk.app-key.interface
  (:require [soul-talk.app-key.handler :as handler]
            [soul-talk.app-key.spec :as spec]))
(def token spec/token)
(def create-app-key spec/create-app-key)

(defn auth-app-key [token]
  (handler/auth-app-key token))

(defn gen-app-key []
  (handler/gen-app-key))

(defn save-app-key [app-key]
  (handler/save-app-key app-key))

(defn delete-app-key [id]
  (handler/delete-app-key id))

(defn load-app-key [req]
  (handler/load-app-keys req))

(defn query-app-key [req]
  (handler/query-app-key req))
