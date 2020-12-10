(ns soul-talk.utils
  (:require [ring.util.http-response :as resp]
            [crypto.random :refer [url-part]]
            [java-time.local :as l]
            [clojure.tools.logging :as log]))

(defn now []
  (l/local-date-time))

(defn gen-token []
  (url-part 32))

(defn parse-int [s]
  (if s
    (Integer/parseInt (re-find #"-?\d+" s))
    0))

(defn ok
  ([] (ok nil nil))
  ([param] (if (map? param)
             (ok nil param)
             (ok param nil)))
  ([msg data] (resp/ok (merge {:result  :ok
                               :message (or msg "操作成功")}
                         data))))

(defn bad-request
  ([] (bad-request nil nil))
  ([msg] (bad-request msg nil))
  ([msg data] (resp/bad-request
                (merge {:result  :error
                        :message (or msg "请求错误，请检查请求参数。")}
                  data))))

(defn unauthorized
  ([] (unauthorized nil nil))
  ([msg] (unauthorized msg nil))
  ([msg data] (resp/unauthorized
                (merge {:result  :error
                        :message (or msg "认证失败，请重新登录或者联系管理员.")}
                  data))))

(defn forbidden
  ([] (forbidden nil nil))
  ([msg] (forbidden msg nil))
  ([msg data] (resp/forbidden
                (merge {:result  :error
                        :message (or msg "非法请求，请检查用户权限。")}
                  data))))

(defn internal-server-error
  ([] (internal-server-error nil nil))
  ([msg] (internal-server-error msg nil))
  ([msg data] (resp/internal-server-error
                (merge {:result  :error
                        :message (or msg "出现内部错误，请联系管理员")}
                  data))))

(defn enhance-your-calm
  ([] (enhance-your-calm nil))
  ([body] (resp/enhance-your-calm body)))

(defn log-error
  [^Exception e data request-or-response type]
  (log/error "error type： -- " type)
  (log/error "error case： -- " (.printStackTrace e)))

(defn parse-header [request]
  (-> request))


(defn make-tree
  ([coll] (let [root {:id 0 :name "根目录"}]
            (assoc root :children (make-tree root coll))))
  ([root coll]
   (for [x coll :when (= (:pid x) (:id root))]
     (assoc x :children (make-tree x coll)))))