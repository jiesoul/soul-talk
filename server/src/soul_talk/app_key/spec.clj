(ns soul-talk.app-key.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]
            [spec-tools.core :as st]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(def token
  (st/spec {:spec        (s/and string? #(<= 32 (count %)))
            :type        :string
            :description "key"
            :reason      "api Key 必须大于等于64位"}))

(def app-name
  (st/spec {:spec        (s/and string? #(not (str/blank? %)))
            :type        :string
            :description "app-name"
            :reason      "名称不能为空"}))

(def create-app-key
  (ds/spec {:name :app-key/create-app-key
            :spec {:token    token
                   :app_name app-name
                   :create_by spec/id}}))