(ns soul-talk.series.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def create-series
  (ds/spec {:name :series/create-series
            :spec {:name spec/non-empty-string?
                   :description spec/non-empty-string?
                   :create_by spec/id
                   :update_by spec/id}}))

(def update-series
  (ds/spec {:name :series/create-series
            :spec {:id spec/id
                   :name spec/non-empty-string?
                   :description spec/non-empty-string?
                   :update_by spec/id}}))
