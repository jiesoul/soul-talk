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
  (ds/spec {:name :tag/add
            :spec {:name spec/username?}}))