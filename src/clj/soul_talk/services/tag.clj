(ns soul-talk.services.tag
  (:require [soul-talk.handlers.tag :as tag]
            [soul-talk.handlers.auth :refer [authenticated]]
            [soul-talk.handlers.common :as common]
            [compojure.api.sweet :refer [context POST]]))

(def tag-routes
  (context "/tags" []

    (POST "/add" []
      :return ::common/Result
      :body [tag tag/Tag]
      :summary "create category"
      (tag/save-tag! tag))))