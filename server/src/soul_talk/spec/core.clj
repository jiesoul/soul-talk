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
(s/def ::result keyword?)
(s/def ::message string?)
(s/def ::Result (s/keys :req-un [::result]
                  :opt-un [::message]))

(s/def ::page int?)
(s/def ::pre-page int?)
(s/def ::Pagination (s/keys :opt-un [::page ::pre-page]))

(def result
  (st/spec {:spec keyword?
            :type :keyword
            :description ":ok 表示成功， :error 表示发生错误。"}))

(def message
  (st/spec {:spec string?
            :type :string
            :description "返回消息"}))

(def Result
  (ds/spec {:name :core/Result
            :spec ::Result}))

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