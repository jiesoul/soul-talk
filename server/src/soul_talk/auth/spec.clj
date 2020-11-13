(ns soul-talk.auth.spec
  (:require [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def login
  (ds/spec {:name :auth/login
            :spec {:email spec/email?
                   :password spec/password?}}))

(def register
  (ds/spec {:name :auth/register
            :spec {:email spec/email?
                   :username spec/username?
                   :password spec/password?}}))

(def logout
  (ds/spec {:name :auth/logout
            :spec {:user_id spec/id}}))