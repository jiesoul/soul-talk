(ns soul-talk.menu.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]))

(def create-menu
  (ds/spec {:name :menu/create
            :spec {:name spec/non-empty-string?
                   (ds/opt :url) (ds/maybe spec/uri-string?)
                   :pid int?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))

(def update-menu
  (ds/spec {:name :menu/update
            :spec {:id spec/id
                   :name spec/non-empty-string?
                   (ds/opt :url) (ds/maybe spec/uri-string?)
                   :pid int?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))