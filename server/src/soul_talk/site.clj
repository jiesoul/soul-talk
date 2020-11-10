(ns soul-talk.site
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.middleware :as m]
            [soul-talk.auth.interface :refer [backend]]
            [soul-talk.api-key.routes :as api-key]
            [soul-talk.data-dic.routes :as data-dic]
            [soul-talk.auth.routes :as auth]
            [soul-talk.user.routes :as user]
            [soul-talk.tag.routes :as tag]
            [soul-talk.article.routes :as article]))

;; 多重方法用来注入中间件
(defmethod restructure-param :auth-rules
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
        auth/private-routes)
      (context "" []
        :auth-rules backend
        data-dic/private-routes
        api-key/private-routes
        user/private-routes
        tag/private-routes
        article/private-routes)
      )))

