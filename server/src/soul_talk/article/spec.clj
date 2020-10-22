(ns soul-talk.article.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]))

(def create-article
  (ds/spec {:name :core/create-article
            :spec {:title spec/non-empty-string?
                   :description spec/non-empty-string?
                   :body spec/non-empty-string?
                   :userId int?
                   (ds/opt :tagList) [spec/non-empty-string?]}}))

(def update-article
  (ds/spec {:name :core/update-article
            :spec {(ds/opt :title) spec/non-empty-string?
                   (ds/opt :description) spec/non-empty-string?
                   (ds/opt :body) spec/non-empty-string?}}))

(def article
  (ds/spec {:name :core/article
            :spec {:id               pos-int?
                   :title            spec/non-empty-string?
                   :description      spec/non-empty-string?
                   :body             spec/non-empty-string?
                   :update_at         string?
                   :create_at         string?
                   (ds/opt :tagList) [spec/non-empty-string?]}}))
(def visible-article
  (ds/spec {:name :core/visible-article
            :spec {:article article}}))