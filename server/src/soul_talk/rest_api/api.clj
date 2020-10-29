(ns soul-talk.rest-api.api
  (:require [ring.util.http-response :as resp]
            [compojure.api.sweet :refer :all]
            [compojure.api.exception :as ex]
            [compojure.api.meta :refer [restructure-param]]
            [compojure.core :refer [wrap-routes]]
            [soul-talk.rest-api.handler :as h]
            [soul-talk.rest-api.middleware :as m]
            [taoensso.timbre :as log]))

;; 错误处理
(defn exception-handler [f type]
  (fn [^Exception e data request]
    (log/error (str "**************错误信息：" (.getMessage e)))
    (f {:result :error :message (str "发生未知错误"), :type type})))

;;
(defn request-validation-handler [f type]
  (fn [^Exception e data req]
    (log/error " 请求发生错误：" (.getMessage e))
    (let [message (->> data
                    :problems
                    :clojure.spec.alpha/problems
                    (map :reason))]
      (f {:result :error :message message}))))

(defn response-validation-handler [f type]
  (fn [^Exception e data resp]
    (log/error " 响应发生错误：" (.getMessage e))
    (let [message (->> data
                    :problems
                    :clojure.spec.alpha/problems)]
      (f {:result :error :message message}))))

(defn request-parsing-handler
  [^Exception ex data req]
  (let [cause (.getCause ex)
        original (.getCause cause)]
    (resp/bad-request
      (merge (select-keys data [:type :format :charset])
        (if original {:original (.getMessage original)})
        {:message (.getMessage cause)}))))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-rule rule]))

(def exceptions-config
  {:handlers {::calm                    (exception-handler resp/enhance-your-calm :calm)
              java.sql.SQLException     (exception-handler resp/internal-server-error :sql)
              ::ex/request-validation   (request-validation-handler resp/bad-request :error)
              ::ex/request-parsing      (ex/with-logging ex/request-parsing-handler :info)
              ::ex/response-validation  (response-validation-handler resp/internal-server-error  :error)
              ::ex/default              (exception-handler resp/internal-server-error :unknown)}})

(def swagger-config
  {:ui   "/api-docs"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}}
   :data {:info {:version "1.0.0"
                 :title       "Self Site API"
                 :description "Self Site API"
                 :contact {:name "jiesoul"
                           :email "jiesoul@gmail.com"
                           :url "http://www.jiesoul.com"}}
          :tags [{:name "登陆" :description "用户登陆相关API"}
                 {:name "用户" :description "用户信息相关API"}
                 {:name "标签" :description "标签相关API"}
                 {:name "文章" :description "文章相关API"}
                 {:name "评论" :description "评论相关API"}
                 ]}})

(def api-config
  {:exceptions exceptions-config
   :coercion :spec
   :swagger swagger-config})

(def api-routes
  (api
    api-config
    (context "/api/v1" []
      :tags ["api version 1"]
      h/public-routes
      h/private-routes)))

