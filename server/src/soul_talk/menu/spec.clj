(ns soul-talk.menu.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]))

(def create-menu
  (ds/spec {:name :menu/create
            :spec {:id spec/non-empty-string?
                   :name spec/non-empty-string?
                   (ds/opt :url) (ds/maybe string?)
                   :pid spec/non-empty-string?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))

(def update-menu
  (ds/spec {:name :menu/update
            :spec {:id spec/non-empty-string?
                   :name spec/non-empty-string?
                   (ds/opt :url) (ds/maybe string?)
                   :pid spec/non-empty-string?
                   (ds/opt :note) (ds/maybe spec/non-empty-string?)}}))