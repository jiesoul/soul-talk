(ns soul-talk.site-info.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]
            [cuerdas.core :as str]))

(def update-site-info
  (ds/spec {:name :site-info/update
            :spec {:name                 string?
                   (ds/opt :description) string?
                   (ds/opt :tags)        string?
                   (ds/opt :author)      string?}}))
