(ns soul-talk.routes.files
  (:require [soul-talk.routes.common :refer [handler]]
            [taoensso.timbre :as log]
            [ring.util.http-response :as resp]))

(handler upload-md! [req]
  (log/info req)
  (let [md-str req]
    (-> {:result :ok
         :md-str md-str}
      resp/ok)))
