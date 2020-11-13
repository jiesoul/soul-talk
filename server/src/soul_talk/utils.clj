(ns soul-talk.utils
  (:require [ring.util.http-response :as resp]
            [crypto.random :refer [base64]]
            [taoensso.timbre :as log]))

(defn gen-token
  []
  (base64 32))

(defn parse-int [s]
  (if s
    (Integer/parseInt (re-find #"-?\d+" s))
    0))

(defn ok
  ([] (ok nil nil))
  ([param] (if (map? param)
             (ok nil param)
             (ok param nil)))
  ([msg data] (resp/ok {:result  :ok
                        :message (or msg "操作成功")
                        :data    data})))

(defn bad-request
  ([] (bad-request nil nil))
  ([msg] (bad-request msg nil))
  ([msg data] (resp/bad-request {:result  :error
                                 :message (or msg "请求错误，请检查请求参数。")
                                 :data    data})))

(defn unauthorized
  ([] (unauthorized nil nil))
  ([msg] (unauthorized msg nil))
  ([msg data] (resp/unauthorized {:result  :error
                                  :message (or msg "认证失败，请重新登录或者联系管理员.")
                                  :data    data})))

(defn forbidden
  ([] (forbidden nil nil))
  ([msg] (forbidden msg nil))
  ([msg data] (resp/forbidden {:result  :error
                    :message (or msg "非法请求，请检查用户权限。")
                    :data    data})))

(defn internal-server-error
  ([] (internal-server-error nil nil))
  ([msg] (internal-server-error msg nil))
  ([msg data] (resp/internal-server-error {:result  :error
                                           :message (or msg "出现内部错误，请联系管理员")
                                           :data    data})))

(defn parse-header [request]
  (-> request))