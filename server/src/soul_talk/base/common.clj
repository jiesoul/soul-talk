(ns soul-talk.base.common
  (:require [ring.util.http-response :refer [internal-server-error]]
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

(defn coll->str [coll]
  (subs
    (reduce #(str %1 "," %2) "" coll) 1))



