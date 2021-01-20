(ns soul-talk.menu.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]))

(def create-menu
  (ds/spec {:name :menu/create
            :spec {
                   :name spec/non-empty-string?
                   (ds/opt :url) (ds/maybe string?)
                   :pid spec/id
                   (ds/opt :note) (ds/maybe string?)
                   :create_by spec/id}}))

(def update-menu
  (ds/spec {:name :menu/update
            :spec {
                   :name spec/non-empty-string?
                   (ds/opt :url) (ds/maybe string?)
                   :pid spec/id
                   (ds/opt :note) (ds/maybe string?)
                   :update_by spec/id}}))