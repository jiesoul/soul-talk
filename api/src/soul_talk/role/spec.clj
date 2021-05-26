(ns soul-talk.role.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]))

(def create-role
  (ds/spec {:name :role/create
            :spec {:name spec/non-empty-string?
                   :create_by int?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))

(def update-role
  (ds/spec {:name :role/update
            :spec {:id spec/id
                   :update_by int?
                   :name spec/non-empty-string?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))