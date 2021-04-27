(ns soul-talk.site-info.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]
            [cuerdas.core :as str]))

(def update-site-info
  (ds/spec {:name :site-info/update
            :spec {:id                   spec/id
                   :name                 spec/non-empty-string?
                   (ds/opt :description) (ds/maybe spec/non-empty-string?)
                   (ds/opt :tags) (ds/maybe spec/non-empty-string?)
                   :author               spec/non-empty-string?}}))