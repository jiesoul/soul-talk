(ns soul-talk.api-key.interface
  (:require [soul-talk.api-key.handler :as handler]
            [soul-talk.api-key.spec :as spec]))
(def token spec/token)
(def create-api-key spec/create-api-key)
(def auth-api-key handler/auth-api-key)

(defn gen-api-key []
  (handler/gen-api-key))

(defn save-api-key [api-key]
  (handler/save-api-key api-key))

(defn delete-api-key [id]
  (handler/delete-api-key id))

(defn load-all-api-key []
  (handler/load-all-api-keys))
