(ns soul-talk.data-dic.interface
  (:require [soul-talk.data-dic.handler :as handler]
            [soul-talk.data-dic.spec :as spec]))

(def create-data-dic spec/create-data-dic)
(def update-data-dic spec/update-data-dic)

(defn load-data-dic-page [req]
  (handler/load-data-dic-page req))

(defn save-data-dic [data-dic]
  (handler/save-data-dic data-dic))

(defn update-data-dic [data-dic]
  (handler/update-data-dic data-dic))

(defn delete-data-dic-by-id [id]
  (handler/delete-data-dic-by-id id))

(defn load-data-dic-by-pid [pid]
  (handler/load-data-by-pid pid))

(defn get-data-dic-by-id [id]
  (handler/get-data-dic-by-id id))