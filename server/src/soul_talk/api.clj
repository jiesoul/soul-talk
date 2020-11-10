(ns soul-talk.api
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :as m]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.api-key.interface :refer [auth-api-key]]
            [soul-talk.auth.routes :as auth]
            [soul-talk.user.routes :as user]
            [soul-talk.tag.routes :as tag]
            [soul-talk.article.routes :as article]))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

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
          :tags [{:name "用户" :description "用户信息相关API"}
                 {:name "标签" :description "标签相关API"}
                 {:name "文章" :description "文章相关API"}]}})

(def api-config
  {:exceptions m/exceptions-config
   :ring-swagger {:ignore-missing-mappings? true}
   :coercion :spec
   :swagger swagger-config})

(def api-routes
  (api
    api-config

    (context "" []
      :tags ["api version 1"]

      (context "/api/v1" []
        :auth-rules auth-api-key

        user/public-routes
        tag/public-routes
        article/public-routes))))

