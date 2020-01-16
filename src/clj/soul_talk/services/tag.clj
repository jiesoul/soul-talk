(ns soul-talk.services.tag
  (:require [soul-talk.handlers.tag :as tag]
            [soul-talk.handlers.auth :refer [authenticated]]
            [compojure.api.sweet :refer [context POST]]))

(def routes
  (context "/tags" []

    (POST "/add" []
      :return ::Result
      :body [tag tag/Tag]
      :summary "create category"
      (tag/save-tag! tag))))