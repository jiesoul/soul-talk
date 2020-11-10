(ns soul-talk.site
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.middleware :as m]
            [soul-talk.auth.interface :refer [backend]]
            [soul-talk.data-dic.routes :as data-dic]
            [soul-talk.auth.routes :as auth]
            [soul-talk.user.routes :as user]
            [soul-talk.tag.routes :as tag]
            [soul-talk.article.routes :as article]))

(def site-config
  {:exceptions m/exceptions-config
   :coercion :spec})

(def site-routes
  (->
    (api
      site-config
      (context "" []
        data-dic/private-routes
        auth/private-routes
        user/private-routes
        tag/private-routes
        article/private-routes)
      )
    (m/wrap-auth backend)))

