(ns soul-talk.api
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :as m]
            [soul-talk.user.routes :as user]
            [soul-talk.tag.routes :as tag]
            [soul-talk.article.routes :as article]))

; 多重方法用来注入中间件
;; 如果需要这里添加路由，请拷贝此方法到路由文件。
(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(def swagger-config
  {:ui   "/api/v1/api-docs"
   :spec "/swagger.json"
   :options {:ui {:validatorUrl nil}}
   :data {:info {:version "1.0.0"
                 :title       "个人网站公共API"
                 :description "提供网站部分数据的API"
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

        tag/public-routes
        user/public-routes
        article/public-routes))))

