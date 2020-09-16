(ns soul-talk.rest-api.api
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [compojure.api.exception :as ex]
            [compojure.core :refer [wrap-routes]]
            [soul-talk.rest-api.handler :as h]))

;; 错误处理
(defn exception-handler [f type]
  (fn [^Exception e data request]
    (f {:message (.getMessage e), :type type})))



(def exceptions-config
  {:handlers {::calm                  (exception-handler enhance-your-calm :calm)
              java.sql.SQLException   (exception-handler internal-server-error :sql)
              ::ex/request-validation (ex/with-logging ex/request-parsing-handler :info)
              ::ex/default (exception-handler internal-server-error :unknown)}})

(def swagger-config
  {:ui   "/api-docs"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}}
   :data {:info {:version "1.0.0"
                 :title       "Soul Talk API"
                 :description "public API"
                 :contact {:name "jiesoul"
                           :email "jiesoul@gmail.com"
                           :url "http://www.jiesoul.com"}}
          :tags [{:name "user" :description "user info"}]}})

(def api-config
  {:exceptions exceptions-config
   :coercion   :spec
   :swagger swagger-config })


(def api-routes
  (api
    api-config
    (context "/api/v1" []
      :tags ["api version 1"]
      h/auth-routes
      h/user-routes
      h/article-routes
      h/tag-routes)))

