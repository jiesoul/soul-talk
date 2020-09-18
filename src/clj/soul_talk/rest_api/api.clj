(ns soul-talk.rest-api.api
  (:require [ring.util.http-response :refer :all]
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
    ;(log/log! type e (.getMessage e))
    (f {:message (.getMessage e), :type type})))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-rule rule]))

(defn handle-missing-routes-fn []
  )

(def exceptions-config
  {:handlers {::calm                    (exception-handler enhance-your-calm :calm)
              java.sql.SQLException     (exception-handler internal-server-error :sql)
              ::ex/request-validation   (ex/with-logging ex/request-parsing-handler :error)
              ::ex/request-parsing      (ex/with-logging ex/request-parsing-handler :info)
              ::ex/response-validation  (exception-handler ex/response-validation-handler :error)
              ::ex/default              (exception-handler internal-server-error :unknown)}})

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
          :tags [{:name "user" :description "user info"}]}})



(def api-config
  {
   ;:api {:invalid-routes-fn handle-missing-routes-fn}
   :exceptions exceptions-config
   :coercion   :spec
   :swagger swagger-config })


(def api-routes
  (api
    api-config
    (context "/api/v1" []
      :tags ["api version 1"]
      h/public-routes
      h/private-routes)))

