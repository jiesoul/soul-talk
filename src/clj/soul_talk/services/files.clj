(ns soul-talk.services.files
  (:require [soul-talk.handlers.files :as files]
            [buddy.auth :refer [authenticated?]]
            [soul-talk.handlers.common :as common]
            [compojure.api.sweet :refer [context POST]]))

(def file-routes
  (context "/files" []

    (POST "/md" req
      :auth-rules authenticated?
      :return ::common/Result
      :summary "upload md file to str"
      (files/upload-md! req))))
