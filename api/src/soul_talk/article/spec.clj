(ns soul-talk.article.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]))

(def create-article
  (ds/spec {:name :article/create-article
            :spec {:title                string?
                   :body                 spec/non-empty-string?
                   :category_id              spec/id
                   :create_by            spec/id
                   :update_by             spec/id}}))

(def update-article
  (ds/spec {:name :article/update-article
            :spec {:id                   spec/non-empty-string?
                   :update_by            int?
                   :title                spec/non-empty-string?
                   :body                 spec/non-empty-string?
                   :category_id              spec/id}}))

(def article
  (ds/spec {:name :article/article
            :spec {:id               spec/non-empty-string?
                   :title            spec/non-empty-string?
                   :body             spec/non-empty-string?
                   :update_by        spec/id
                   :create_by        spec/id}}))

(def article-tag
  (ds/spec {:name :article/article-tag
            :spec {:article_id string?
                   :tag_id int?}}))


(def article-category
  (ds/spec {:name :article/article-category
            :spec {:article_id string?
                   :category_id int?}}))

(def article-comment
  (ds/spec {:name :article/create-comment
            :spec {:article_id spec/non-empty-string?
                   :body spec/non-empty-string?
                   :create_by_name spec/non-empty-string?
                   :create_by_email spec/email?}}))
