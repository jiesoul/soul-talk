(ns soul-talk.data-dic.handler
  (:require [soul-talk.data-dic.db :as db]
            [soul-talk.utils :as utils]
            [soul-talk.pagination :as p]
            [java-time.local :as l]
            [soul-talk.data-dic.spec :as spec]
            [cambium.core :as log]))

(def create-data-dic spec/create-data-dic)
(def update-data-dic spec/update-data-dic)

(defn load-all []
  (let [data-dices (db/get-data-dic-all)]
    (utils/ok "加载成功" {:data-dices data-dices})))

(defn save-data-dic [{:keys [id pid] :as data-dic}]
  (if (= id pid)
    (utils/bad-request "id pid 不能相同")
    (if-let [dd (db/get-data-dic-by-id id)]
      (utils/bad-request (str "id: " id " 已经存在"))
      (let [data-dic (db/save-data-dic data-dic)]
        (log/info "data-dic: " data-dic)
        (utils/ok {:data-dic data-dic})))))

(defn update-data-dic [{:keys [id] :as data-dic}]
  (let [_ (db/update-data-dic (assoc data-dic :update_at (l/local-date-time)))
        data-dic (db/get-data-dic-by-id id)]
    (utils/ok {:data-dic data-dic})))

(defn delete-data-dic-by-id [id]
  (let [data-dices (db/get-data-dices-by-pid id)]
    (if (empty? data-dices)
      (do (db/delete-data-dic-by-id id)
          (utils/ok "删除成功"))
      (utils/bad-request "请先删除子类。"))))

(defn load-data-dic-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [data-dices total] (db/load-data-dic-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:data-dices data-dices
               :pagination pagination
               :query-params params})))

(defn load-data-dices-by-pid [pid]
  (let [data-dices (db/load-data-dices-by-pid pid)]
    (utils/ok {:data-dices data-dices})))

(defn get-data-dic-by-id [id]
  (let [data-dic (db/get-data-dic-by-id id)]
    (utils/ok {:data-dic data-dic})))

(defn load-all-data-dices [req]
  (utils/ok {:data-dices (db/get-data-dic-all)}))
