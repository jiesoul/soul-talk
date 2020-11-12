(ns soul-talk.collect-link.spec
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

(def create-collect-link
  (ds/spec {:name :collect-link/create-collect-link
            :spec {:title title
                   (ds/opt :url) (ds/maybe spec/uri-string?)
                   (ds/opt :image) (ds/maybe spec/uri-string?)
                   :create_by spec/id
                   :update_by spec/id
                   }}))

(def update-collect-link
  (ds/spec {:name :collect-link/update-collect-link
            :spec {:id spec/id
                   :title title
                   (ds/opt :url) (ds/maybe spec/uri-string?)
                   (ds/opt :image) (ds/maybe spec/uri-string?)
                   :update_by spec/id
                   }}))