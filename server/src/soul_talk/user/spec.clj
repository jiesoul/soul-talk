(ns soul-talk.user.spec
  (:require [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def id
  (st/spec {:spec        pos-int?
            :type        :long
            :description "user id"
            :reason "id 必须为非负整数！"}))

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
                   :user_id id}}))

(def update-user
  (ds/spec {:name :user/update-user
            :spec {:email spec/email?
                   :username spec/username?
                   :password spec/password?
                   :image (ds/maybe spec/uri-string?)
                   :bio (ds/maybe spec/non-empty-string?)}
            :keys-default ds/opt}))

(def update-password
  (ds/spec {:name :user/update-password
            :spec {:oldPassword spec/password?
                   :newPassword spec/password?
                   :confirmPassword spec/password?}}))

(def user
  (ds/spec {:name :user/User
            :spec {:id             id
                   :email          spec/email?
                   :username       spec/username?
                   (ds/opt :image) spec/uri-string?
                   (ds/opt :bio)   spec/non-empty-string?}}))

(def profile-user
  (ds/spec {:name :user/profile-user
            :spec {:username spec/username?
                   (ds/opt :image) spec/uri-string?
                   (ds/opt :bio)   spec/non-empty-string?}}))

(def visible-user
  (ds/spec {:name :user/visible-user
            :spec {:user user}}))
