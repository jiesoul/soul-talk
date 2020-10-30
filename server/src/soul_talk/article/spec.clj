(ns soul-talk.article.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]))

(def create-article
  (ds/spec {:name :core/create-article
            :spec {:title                string?
                   (ds/opt :description) string?
                   :body                 spec/non-empty-string?
                   :create_by            int?
                   (ds/opt :tagList)     (s/coll-of string?)}}))

(def update-article
  (ds/spec {:name :core/update-article
            :spec {(ds/opt :title) spec/non-empty-string?
                   (ds/opt :description) spec/non-empty-string?
                   (ds/opt :body) spec/non-empty-string?}}))

(def article
  (ds/spec {:name :core/article
            :spec {:id               spec/non-empty-string?
                   :title            spec/non-empty-string?
                   :description      spec/non-empty-string?
                   :body             spec/non-empty-string?
                   :update_at        string?
                   :create_at        string?
                   (ds/opt :tagList) (s/coll-of string?)}}))
(def visible-article
  (ds/spec {:name :core/visible-article
            :spec {:article article}}))
