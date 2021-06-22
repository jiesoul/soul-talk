(ns soul-talk.reply.spec
  (:require [soul-talk.spec.core :as spec]
            [spec-tools.data-spec :as ds]
            [clojure.spec.alpha :as s]))

(def create-reply
  (ds/spec {:name :reply/create-reply
            :spec {:topic_id spec/non-empty-string?
                   :user_name             spec/non-empty-string?
                   :content                 spec/non-empty-string?}}))

(def reply
  (ds/spec {:name :reply/reply
            :spec {:id               spec/non-empty-string?
                   :title            spec/non-empty-string?
                   :body             spec/non-empty-string?
                   :update_by        int?
                   :create_by        int?}}))

(def reply-tag
  (ds/spec {:name :reply/reply-tag
            :spec {:reply_id string?
                   :tag_id int?}}))


(def reply-category
  (ds/spec {:name :reply/reply-category
            :spec {:reply_id string?
                   :category_id int?}}))

(def reply-comment
  (ds/spec {:name :reply/create-comment
            :spec {:reply_id spec/non-empty-string?
                   :body spec/non-empty-string?
                   :create_by_name spec/non-empty-string?
                   :create_by_email spec/email?}}))
