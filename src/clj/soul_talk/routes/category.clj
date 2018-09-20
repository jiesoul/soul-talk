(ns soul-talk.routes.category
  (:require [clojure.spec.alpha :as s]
            [soul-talk.models.category-db :as category-db]
            [soul-talk.routes.common :refer [handler]]
            [ring.util.http-response :as resp]))


(s/def ::name string?)

(def Category
  (s/def ::Category (s/keys :req-un [::name])))


(handler get-all-categories []
  (let [categories (category-db/get-categories)]
    (resp/ok {:result :ok
              :categories categories})))

(handler save-category! [category]
  (do
    (category-db/save-category! category)
    (-> {:result :ok}
      resp/ok)))
