(ns soul-talk.site-info.handler
  (:require [soul-talk.utils :as utils]
            [soul-talk.site-info.db :as db]
            [soul-talk.site-info.spec :as spec]))

(def update-site-info spec/update-site-info)

(defn update-site-info!
  [site-info]
  (let [rs (db/update! site-info)]
    (utils/ok {:site-info rs})))

(defn get-site-info
  [id]
  (let [site-info (db/get-by-id id)]
    (utils/ok {:site-info site-info})))
