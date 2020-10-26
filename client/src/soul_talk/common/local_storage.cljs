(ns soul-talk.common.local-storage
  (:require [taoensso.timbre :as log]))

(defonce login-user-key "login-user")
(defonce auth-token-key "auth-token")

(defn set-item!
  [key val]
  (->> val
    (clj->js)
    (js/JSON.stringify)
    (.setItem (.-localStorage js/window) key)))

(defn get-item
  [key]
  (js->clj
    (->> key
      (.getItem (.-localStorage js/window))
      (.parse js/JSON))
    :keywordize-keys true))

(defn remove-item!
  [key]
  (log/debug "from localstorage remove key: " key)
  (.removeItem (.-localStorage js/window) key))
