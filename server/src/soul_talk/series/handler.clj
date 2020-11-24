(ns soul-talk.series.handler
  (:require [soul-talk.series.db :as db]
            [soul-talk.utils :as utils]
            [soul-talk.pagination :as p]
            [soul-talk.series.spec :as spec]
            [java-time.local :as l]))

(def create-series spec/create-series)
(def update-series spec/update-series)

(defn load-series-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [series total] (db/load-series-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:series series
               :pagination pagination
               :query-str params})))

(defn save-series [series]
  (let [now (l/local-date-time)
        series (db/save-series (assoc series :create_at now :update_at now))]
    (utils/ok {:series series})))

(defn update-series [series]
  (let [series (db/update-series series)]
    (utils/ok {:series series})))

(defn delete-series [id]
  (let [result (db/delete-series id)]
    (utils/ok "删除成功")))

(defn get-series-by-id [id]
  (let [series (db/get-series-by-id id)]
    (if series
      (utils/ok {:series series})
      (utils/bad-request "not find series by id " id))))
