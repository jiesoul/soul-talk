(ns soul-talk.handlers.common
  (:require [ring.util.http-response :refer [internal-server-error]]
            [clojure.spec.alpha :as s]
            [taoensso.timbre :as log]))

(defmacro handler
  {:style/indent :defn}
  [fn-name args & body]
  `(defn ~fn-name ~args
     (try
       ~@body
       (catch Throwable t#
         (log/error t# "error handling request")
         (internal-server-error {:result :error
                                 :message "请求发生错误"})))))


(s/def ::id int?)
(s/def ::result keyword?)
(s/def ::message string?)
(s/def ::Result (s/keys :req-un [::result]
                  :opt-un [::message]))

(s/def ::page int?)
(s/def ::pre-page int?)
(s/def ::Pagination (s/keys :opt-un [::page ::pre-page]))
