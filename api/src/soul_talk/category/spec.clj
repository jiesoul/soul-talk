(ns soul-talk.category.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def create-category
  (ds/spec {:name :category/create-category
            :spec {:name spec/non-empty-string?
                   :create_by spec/id
                   :update_by spec/id}}))

(def update-category
  (ds/spec {:name :category/update-category
            :spec {:id spec/id
                   :name spec/non-empty-string?
                   :update_by spec/id}}))
