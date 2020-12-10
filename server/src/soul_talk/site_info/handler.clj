(ns soul-talk.site-info.handler
  (:require [soul-talk.utils :as utils]
            [soul-talk.site-info.db :as db]
            [soul-talk.site-info.spec :as spec]
            [clojure.tools.logging :as log]))

(def update-site-info spec/update-site-info)

(defn update-site-info!
  [site-info]
  (let [id (:id site-info)
        rs (db/update! site-info)
        site-info (db/get-by-id id)]
    (log/debug "===" site-info)
    (utils/ok {:site-info site-info})))

(defn get-site-info
  [id]
  (let [site-info (db/get-by-id id)]
    (utils/ok {:site-info site-info})))
