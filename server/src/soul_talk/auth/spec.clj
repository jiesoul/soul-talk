(ns soul-talk.auth.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]
            [spec-tools.core :as st]
            [clojure.spec.alpha :as s]
            [cuerdas.core :as str]))

(def login
  (ds/spec {:name :user/login
            :spec {:email spec/email?
                   :password spec/password?}}))

(def register
  (ds/spec {:name :user/register
            :spec {:email spec/email?
                   :username spec/username?
                   :password spec/password?}}))

(def auth-token
  (ds/spec {:name :user/auth-token
            :spec {:token      (st/spec {:spec        (s/and string? #(not (str/blank? %)))
                                      :type        :string
                                      :description "Not empty string spec. Check with clojure.string/blank?"
                                      :reason      "id 不能为空"})
                   :user_id spec/id}}))