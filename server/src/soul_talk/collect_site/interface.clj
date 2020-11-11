(ns soul-talk.collect-site.interface
  (:require [soul-talk.collect-site.handler :as handler]
            [soul-talk.collect-site.spec :as spec]))
(def token spec/token)
(def create-collect-site spec/create-collect-site)

(defn auth-collect-site [token]
  (handler/auth-collect-site token))

(defn gen-collect-site []
  (handler/gen-collect-site))

(defn save-collect-site [collect-site]
  (handler/save-collect-site collect-site))

(defn delete-collect-site [id]
  (handler/delete-collect-site id))

(defn load-collect-site [req]
  (handler/load-collect-sites req))

(defn query-collect-site [req]
  (handler/query-collect-site req))
