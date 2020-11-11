(ns soul-talk.serials.handler
  (:require [soul-talk.serials.db :as db]
            [soul-talk.utils :as utils]))

(defn load-serials []
  (let [serials (db/load-serials)]
    (utils/ok {:serials serials})))

(defn save-serials [serials]
  (let [serials (db/save-serials serials)]
    (utils/ok {:serials serials})))

(defn update-serials [serials]
  (let [serials (db/update-serials serials)]
    (utils/ok {:serials serials})))

(defn delete-serials [id]
  (let [result (db/delete-serials id)]
    (utils/ok "删除成功")))
