(ns soul-talk.user.spec
  (:require [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def id
  (st/spec {:spec pos-int?
            :type :long
            :description "user id"}))

(def login
  (ds/spec {:name :core/login
             :spec {:email spec/email?
                    :password spec/password?}}))

(def register
  (ds/spec {:name :core/register
            :spec {:username spec/username?
                   :email spec/email?
                   :password spec/password?}}))

(def update-user
  (ds/spec {:name :core/update-user
            :spec {:email spec/email?
                   :username spec/username?
                   :password spec/password?
                   :image (ds/maybe spec/uri-string?)
                   :bio (ds/maybe spec/non-empty-string?)}
            :keys-default ds/opt}))

(def update-password
  (ds/spec {:name :core/update-password
            :spec {:oldPassword spec/password?
                   :newPassword spec/password?
                   :confirmPassword spec/password?}}))

(def user
  (ds/spec {:name :core/user
            :spec {:id id
                   :email spec/email?
                   :username spec/username?
                   :image (ds/maybe spec/uri-string?)
                   :bio (ds/maybe spec/non-empty-string?)}}))

(def profile-user
  (ds/spec {:name :core/profile-user
            :spec {:username (ds/maybe spec/username?)
                   :image    (ds/maybe spec/uri-string?)
                   :bio      (ds/maybe spec/non-empty-string?)}}))

(def visible-user
  (ds/spec {:name :core/visible-user
            :spec {:user user}}))
