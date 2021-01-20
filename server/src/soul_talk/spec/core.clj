(ns soul-talk.spec.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]
            [spec-tools.core :as st]
            [spec-tools.data-spec :as ds])
  (:import (java.util UUID)))

(def ^:private email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")
(def ^:private uri-regex #"https?:\/\/(www\.)?[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)")
(def ^:private slug-regex #"^[a-z0-9]+(?:-[a-z0-9]+)*$")

(s/def ::id int?)
(s/def ::description string?)
(s/def ::amount pos-int?)
(s/def ::delivery inst?)
(s/def ::tags (s/coll-of keyword? :into #{}))
(s/def ::item (s/keys :req-un [::description ::tags ::amount]))
(s/def ::items (s/map-of ::id ::item))
(s/def ::location (s/tuple double? double?))
(s/def ::order (s/keys :req-un [::id ::items ::delivery ::location]))

(s/def ::page int?)
(s/def ::pre-page int?)
(s/def ::Pagination (s/keys :opt-un [::page ::pre-page]))

(def id
  (st/spec {:spec        int?
            :description "id"
            :reason "id 必须为非负整数！"}))

(def result
  (st/spec {:spec keyword?
            :type :keyword
            :description ":ok 表示成功， :error 表示发生错误。"}))

(def message
  (st/spec {:spec string?
            :type :string
            :description "返回信息"}))

(def data
  (st/spec {:spec map?
            :type :map
            :description "返回的数据 （k-v 键值对）"}))

(def Result
  (ds/spec {:name :core/Result
            :spec {:result           result
                   (ds/opt :message) (ds/maybe message)
                   (ds/opt :data)    (ds/maybe data)}}))

(def page (st/spec {:spec int?
                    :type :integer
                    :description "当前页"}))
(def pre-page (st/spec {:spec int?
                        :type :integer
                        :description "上一页"}))

(def Pagination
  (ds/spec {:name :core/Pagination
            :spec {:page page
                   :pre-page pre-page}}))

(def non-empty-string?
  (st/spec {:spec        (s/and string? #(not (str/blank? %)))
            :type        :string
            :description "Not empty string spec. Check with clojure.string/blank?"
            :reason "字符串不能为空"}))

(def username?
  (ds/spec {:spec        non-empty-string?
            :type        :string
            :description "非空字符串"
            ;:gen         #(gen/fmap (fn [] (str (UUID/randomUUID)))
            ;                (gen/string-alphanumeric))
            :reason "名称不能为空"
            }))

(def email?
  (st/spec {:spec (s/and string? #(re-matches email-regex %))
            :type :string
            :description "邮箱(xxx@xx.xx)"
            :gen #(gen/fmap (fn [[s1 s2]] (str s1 "@" s2 ".com"))
                    (gen/tuple (gen/string-alphanumeric) (gen/string-alphanumeric)))
            :reason "非法邮件格式"}))

(def uri-string?
  (st/spec {:spec (s/and string? #(re-matches uri-regex %))
            :type :string
            :description "URL(http://xxxx.xxx)"
            :gen #(gen/fmap (fn [[c1 c2]]
                              (let [s1 (apply str c1)
                                    s2 (apply str c2)]
                                (str "http://" s1 "." (subs s2 0 (if (< 3 (count s2)) 3 (count s2))))))
                    (gen/tuple (gen/vector (gen/char-alpha) 2 100) (gen/vector (gen/char-alpha) 2 5)))
            :reason "地址格式错误"}))

(def slug?
  (st/spec {:spec (s/and string? #(re-matches slug-regex %))
            :type :string
            :description "A string spec that conforms to slug-regex"
            :gen #(gen/fmap (fn [[c1 c2]]
                              (let [s1 (str/lower-case (apply str c1))
                                    s2 (str/lower-case (apply str c2))]
                                (str s1 "-" "s2")))
                    (gen/tuple (gen/vector (gen/char-alpha) 2 10) (gen/vector (gen/char-alpha) 2 10)))}))

(def password?
  (st/spec {:spec (s/and string? #(<= 8 (count %)))
            :type :string
            :description "密码大于等于8个字符"
            :reason "密码必须大于等于8位"}))

