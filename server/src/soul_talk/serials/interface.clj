(ns soul-talk.serials.interface
  (:require [soul-talk.serials.handler :as handler]
            [soul-talk.serials.spec :as spec]))

(def create-serials spec/create-serials)
(def update-serials spec/update-serials)

(defn load-serials []
  (handler/load-serials))

(defn save-serials [serials]
  (handler/save-serials serials))

(defn update-serials [serials]
  (handler/update-serials serials))

(defn delete-serials [id]
  (handler/delete-serials id))
