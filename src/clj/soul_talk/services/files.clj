(ns soul-talk.services.files
  (:require [soul-talk.handlers.files :as files]
            [soul-talk.handlers.auth :refer [authenticated]]
            [compojure.api.sweet :refer [context POST]]))

(def routes
  (context "/files" []

    (POST "/md" req
      :auth-rules authenticated
      :return ::Result
      :summary "upload md file to str"
      (files/upload-md! req))))
