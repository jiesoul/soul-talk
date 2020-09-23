(ns soul-talk.tag.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]
            [spec-tools.core :as st]))

(def id
  (st/spec {:spec        pos-int?
            :type        :long
            :description "user id"
            :reason "id "}))

(def tag
  (ds/spec {:name :core/tag
            :spec {:name (st/spec {:spec spec/non-empty-string?
                                   :type string?
                                   :reason "名称不能为空"})}}))