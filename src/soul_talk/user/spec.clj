(ns soul-talk.user.spec
  (:require [spec-tools.core :as st]
            [spec-tools.data-spec :as ds]
            [soul-talk.spec.core :as spec]))

(def id
  (st/spec {:spec        pos-int?
            :type        :long
            :description "user id"
            :reason "id 必须为非负整数！"}))

(def old-password
  (st/spec {:sepc spec/password?
            :type :string
            :description "原始密码"
            :reason "原始密码不能为空或小于8位"}))

(def login
  (ds/spec {:name :core/login
             :spec {:email spec/email?
                    :password spec/password?}}))

(def register
  (ds/spec {:name :core/register
            :spec {:email spec/email?
                   :username spec/username?
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
            :spec {:oldPassword (st/spec {:spec spec/password?
                                          :reason "原始密码不能为空或小于8位"})
                   :newPassword (st/spec {:spec spec/password?
                                          :reason "新密码不能为空或小于8位"})
                   :confirmPassword (st/spec {:spec spec/password?
                                              :reason "确认密码不能为空或小于8位"})}}))

(def user
  (ds/spec {:name :core/user
            :spec {:id id
                   :email spec/email?
                   :username spec/username?
                   :image (ds/opt spec/uri-string?)
                   :bio (ds/opt spec/non-empty-string?)}}))

(def profile-user
  (ds/spec {:name :core/profile-user
            :spec {:username spec/username?}}))

(def visible-user
  (ds/spec {:name :core/visible-user
            :spec {:user user}}))
