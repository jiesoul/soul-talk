(ns soul-talk.collect-site.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]
            [spec-tools.core :as st]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def title
  (st/spec {:spec        (s/and string? #(not (str/blank? %)))
            :type        :string
            :description "标题"
            :reason      "名称不能为空"}))

(def create-collect-site
  (ds/spec {:name :collect-site/create-collect-site
            :spec {:title title
                   (ds/opt :url) (ds/maybe spec/uri-string?)
                   (ds/opt :image) (ds/maybe spec/uri-string?)
                   :create_by spec/id
                   :update_by spec/id
                   }}))

(def update-collect-site
  (ds/spec {:name :collect-site/update-collect-site
            :spec {:id spec/id
                   :title title
                   (ds/opt :url) (ds/maybe spec/uri-string?)
                   (ds/opt :image) (ds/maybe spec/uri-string?)
                   :update_by spec/id
                   }}))


(def collect-site-tag
  (ds/spec {:name :collect-site/article-tag
            :spec {:collect_site_id string?
                   :tag_id int?}}))


(def collect-site-series
  (ds/spec {:name :collect-site/article-series
            :spec {:collect_site_id string?
                   :series_id int?}}))

(def collect-site-comment
  (ds/spec {:name :collect-site/create-comment
            :spec {:collect_site_id spec/non-empty-string?
                   :body spec/non-empty-string?
                   :create_by_name spec/non-empty-string?
                   :create_by_email spec/email?}}))