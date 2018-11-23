(ns soul-talk.routes.category
  (:require [clojure.spec.alpha :as s]
            [soul-talk.models.category-db :as category-db]
            [soul-talk.routes.common :refer [handler]]
            [ring.util.http-response :as resp]
            [soul-talk.models.post-db :as post-db]
            [taoensso.timbre :as log]))

(s/def ::id int?)
(s/def ::name string?)

(def Category
  (s/def ::Category (s/keys :req-un [::name])))


(handler get-all-categories []
  (let [categories (category-db/get-categories)]
    (resp/ok {:result :ok
              :categories categories})))

(handler get-category-by-id [id]
  (let [category (category-db/get-category-by-id id)]
    (-> {:result :ok
         :category category}
        resp/ok)))

(handler save-category! [category]
  (let [co (category-db/save-category! category)]
    (-> {:result :ok
         :category co}
      resp/ok)))

(handler update-category! [category]
  (let [result (category-db/update-category! category)]
    (-> {:result   :ok
         :category category}
        resp/ok)))

(handler delete-category! [id]
  (let [post-count (post-db/get-post-by-category id)]
    (if (pos? post-count)
      (-> {:result :error
           :message "有文章属于这个分类，不能删除！"}
        (resp/bad-request))
      (do
        (category-db/delete-category! id)
        (resp/ok {:result :ok})))))
