(ns soul-talk.site
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :as m]
            [soul-talk.site-info.routes :as site-info]
            [soul-talk.app-key.routes :as app-key]
            [soul-talk.data-dic.routes :as data-dic]
            [soul-talk.menu.routes :as menu]
            [soul-talk.role.routes :as role]
            [soul-talk.auth.routes :as auth]
            [soul-talk.user.routes :as user]
            [soul-talk.series.routes :as series]
            [soul-talk.tag.routes :as tag]
            [soul-talk.article.routes :as article]))

;; 多重方法用来注入中间件
;; 如果需要这里添加路由，请拷贝此方法到路由文件。
(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))

(def site-config
  {:exceptions m/exceptions-config
   :coercion :spec})

(def site-routes
  (->
    (api
      site-config
      (context "" []
        site-info/site-routes
        auth/login-routes
        data-dic/site-routes
        menu/site-routes
        role/site-routes
        app-key/site-routes
        user/private-routes
        series/site-routes
        tag/private-routes
        article/site-routes)
      )))

