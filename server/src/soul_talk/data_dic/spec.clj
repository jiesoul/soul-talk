(ns soul-talk.data-dic.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def create-data-dic
  (ds/spec {:name :data-dics/dreate-date-dic
            :spec {:id spec/non-empty-string?
                   :name spec/non-empty-string?
                   :pid spec/non-empty-string?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))


(def update-data-dic
  (ds/spec {:name :data-dics/update-date-dic
            :spec {:id spec/non-empty-string?
                   :name spec/non-empty-string?
                   :pid spec/non-empty-string?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))
