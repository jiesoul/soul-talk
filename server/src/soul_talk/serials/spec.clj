(ns soul-talk.serials.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def create-serials
  (ds/spec {:name :serials/create-serials
            :spec {:name spec/non-empty-string?
                   :description spec/non-empty-string?
                   :create_by spec/id
                   :update_by spec/id}}))

(def update-serials
  (ds/spec {:name :serials/create-serials
            :spec {:id spec/id
                   :name spec/non-empty-string?
                   :description spec/non-empty-string?
                   :update_by spec/id}}))
