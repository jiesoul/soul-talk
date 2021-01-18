(ns soul-talk.user.spec
  (:require [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def update-user
  (ds/spec {:name :user/update-user
            :spec {:email spec/email?
                   :name spec/username?
                   :password spec/password?
                   :image (ds/maybe spec/uri-string?)
                   :bio (ds/maybe spec/non-empty-string?)}
            :keys-default ds/opt}))

(def update-password
  (ds/spec {:name :user/update-password
            :spec {:old-password spec/password?
                   :new-password spec/password?
                   :confirm-password spec/password?}}))

(def user
  (ds/spec {:name :user/User
            :spec {:id             spec/id
                   :email          spec/email?
                   :name       spec/username?
                   (ds/opt :image) spec/uri-string?
                   (ds/opt :bio)   spec/non-empty-string?}}))

(def profile-user
  (ds/spec {:name :user/profile-user
            :spec {:name spec/username?
                   (ds/opt :image) spec/uri-string?
                   (ds/opt :bio)   spec/non-empty-string?}}))

(def visible-user
  (ds/spec {:name :user/visible-user
            :spec {:user user}}))
