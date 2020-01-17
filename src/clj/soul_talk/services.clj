(ns soul-talk.services
  (:require [ring.util.http-response :refer :all]
            [compojure.api.sweet :refer :all]
            [compojure.api.exception :as ex]
            [soul-talk.services.auth :refer [auth-routes]]
            [soul-talk.services.user :refer [user-routes]]
            [soul-talk.services.category :refer [category-routes]]
            [soul-talk.services.tag :refer [tag-routes]]
            [soul-talk.services.post :refer [post-routes]]
            [soul-talk.services.files :refer [file-routes]]))

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

(def services-routes
  (api
    api-config
    (context "/api/v1" []
      :tags ["api version 1"]
      auth-routes
      user-routes
      category-routes
      tag-routes
      post-routes
      file-routes)))
