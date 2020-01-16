(ns soul-talk.services
  (:require [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :refer [wrap-rule]]
            [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [compojure.api.exception :as ex]
            [clojure.spec.alpha :as s]
            [soul-talk.services.auth :as auth]
            [soul-talk.services.user :as user]
            [soul-talk.services.category :as category]
            [soul-talk.services.tag :as tag]
            [soul-talk.services.post :as post]
            [soul-talk.services.files :as files]))

;; 错误处理
(defn exception-handler [f type]
  (fn [^Exception e data request]
    (f {:message (.getMessage e), :type type})))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [wrap-rule rule]))



(def exceptions-config
  {:handlers {::calm                  (exception-handler enhance-your-calm :calm)
              java.sql.SQLException   (exception-handler internal-server-error :sql)
              ::ex/request-validation (ex/with-logging ex/request-parsing-handler :info)
              ::ex/default (exception-handler internal-server-error :unknown)}})

(def swagger-config
  {:ui   "/api-docs"
   :spec "/swagger.json"
   :data {:info {:title       "Soul Talk API"
                 :description "public API"}
          :tags [{:name "api" :description "apis"}]}})

(def api-config
  {:exceptions exceptions-config
   :coercion   :spec
   :swagger swagger-config })

(def services-routes
  (api
    api-config
    (context "/api" []
      :tags ["api"]
      
      auth/routes
      user/routes
      category/routes
      tag/routes
      post/routes
      files/routes
      )))
