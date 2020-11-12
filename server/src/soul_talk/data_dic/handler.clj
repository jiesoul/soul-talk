(ns soul-talk.data-dic.handler
  (:require [soul-talk.data-dic.db :as db]
            [soul-talk.utils :as utils]
            [soul-talk.pagination :as p]
            [java-time.local :as l]))

(defn load-all []
  (let [data-dics (db/get-data-dic-all)]
    (utils/ok "加载成功" {:data-dics data-dics})))

(defn save-data-dic [data-dic]
  (let [data-dic (db/save-data-dic data-dic)]
    (utils/ok {:data-dic data-dic})))

(defn update-data-dic [{:keys [id] :as data-dic}]
  (let [data-dic (db/update-data-dic (assoc data-dic :update_at (l/local-date-time)))]
    (utils/ok "更新成功")))

(defn delete-data-dic-by-id [id]
  (db/delete-data-dic-by-id id)
  (utils/ok "删除成功"))

(defn load-data-dic-page [req]
  (let [params (:params req)
        pagination (p/create req)
        data-dics (db/load-data-dic-page pagination params)
        totals (db/count-data-dic-page params)
        pagination (p/create-total pagination totals)]
    (utils/ok {:data-dics data-dics
               :pagination pagination
               :query-str params})))

(defn load-data-by-pid [pid]
  (let [data-dics (db/load-data-dics-by-pid pid)]
    (utils/ok {:data-dics data-dics})))

(defn get-data-dic-by-id [id]
  (let [data-dic (db/get-data-dic-by-id id)]
    (utils/ok {:data-dic data-dic})))
