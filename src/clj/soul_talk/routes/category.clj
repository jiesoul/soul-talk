(ns soul-talk.routes.category
  (:require [clojure.spec.alpha :as s]
            [soul-talk.models.category-db :as category-db]
            [soul-talk.routes.common :refer [handler]]
            [ring.util.http-response :as resp]))

(s/def ::id int?)
(s/def ::name string?)

(def Category
  (s/def ::Category (s/keys :req-un [::name])))


(handler get-all-categories []
  (let [categories (category-db/get-categories)]
    (resp/ok {:result :ok
              :categories categories})))

(handler save-category! [category]
  (let [co (category-db/save-category! category)]
    (-> {:result :ok
         :category co}
      resp/ok)))

(handler delete-category! [{:keys [id]}]
         (do
           (category-db/delete-category! id)
           (resp/ok {:result :ok})))
