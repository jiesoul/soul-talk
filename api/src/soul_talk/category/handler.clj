(ns soul-talk.category.handler
  (:require [soul-talk.category.db :as db]
            [soul-talk.utils :as utils]
            [soul-talk.pagination :as p]
            [soul-talk.category.spec :as spec]
            [java-time.local :as l]))

(def create-category spec/create-category)
(def update-category spec/update-category)

(defn load-category-page [req]
  (let [params (:params req)
        pagination (p/create req)
        [category total] (db/load-category-page pagination params)
        pagination (p/create-total pagination total)]
    (utils/ok {:category category
               :pagination pagination
               :query-params params})))

(defn save-category [category]
  (let [now (l/local-date-time)
        category (db/save-category (assoc category :create_at now :update_at now))]
    (utils/ok {:category category})))

(defn update-category [category]
  (let [category (db/update-category (-> category
                                    (assoc :update_at (utils/now))
                                    (dissoc :create_at)))]
    (utils/ok {:category category})))

(defn delete-category [id]
  (let [result (db/delete-category id)]
    (utils/ok "删除成功")))

(defn get-category-by-id [id]
  (let [category (db/get-category-by-id id)]
    (if category
      (utils/ok {:category category})
      (utils/bad-request "not find category by id " id))))
