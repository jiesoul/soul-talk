(ns soul-talk.serials.handler
  (:require [soul-talk.serials.db :as db]
            [soul-talk.utils :as utils]
            [soul-talk.pagination :as p]
            [soul-talk.serials.spec :as spec]))

(def create-serials spec/create-serials)
(def update-serials spec/update-serials)

(defn load-serials-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [serials total] (db/load-serials-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:serials serials
               :pagination pagination
               :query-str params})))

(defn save-serials [serials]
  (let [serials (db/save-serials serials)]
    (utils/ok {:serials serials})))

(defn update-serials [serials]
  (let [serials (db/update-serials serials)]
    (utils/ok {:serials serials})))

(defn delete-serials [id]
  (let [result (db/delete-serials id)]
    (utils/ok "删除成功")))
