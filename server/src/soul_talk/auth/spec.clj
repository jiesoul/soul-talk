(ns soul-talk.auth.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

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
            :spec {:id spec/non-empty-string?
                   :user_id spec/id}}))