(ns soul-talk.article.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]))

(def create-article
  (ds/spec {:name :article/create-article
            :spec {:title                string?
                   :body                 spec/non-empty-string?
                   :create_by            int?
                   :update_by             int?}}))

(def update-article
  (ds/spec {:name :article/update-article
            :spec {:id                   spec/non-empty-string?
                   :update_by            int?
                   :title                spec/non-empty-string?
                   :body                 spec/non-empty-string?}}))

(def article
  (ds/spec {:name :article/article
            :spec {:id               spec/non-empty-string?
                   :title            spec/non-empty-string?
                   :body             spec/non-empty-string?
                   :update_by        int?
                   :create_by        int?}}))

(def article-tag
  (ds/spec {:name :article/article-tag
            :spec {:article_id string?
                   :tag_id int?}}))


(def article-series
  (ds/spec {:name :article/article-series
            :spec {:article_id string?
                   :series_id int?}}))

(def article-comment
  (ds/spec {:name :article/create-comment
            :spec {:article_id spec/non-empty-string?
                   :body spec/non-empty-string?
                   :create_by_name spec/non-empty-string?
                   :create_by_email spec/email?}}))
