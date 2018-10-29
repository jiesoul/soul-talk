(ns soul-talk.routes.files
  (:require [soul-talk.routes.common :refer [handler]]
            [taoensso.timbre :as log]
            [ring.util.http-response :as resp]
            [clojure.java.io :as io]))

(handler upload-md! [{:keys [body params] :as req}]
  (log/info (:params req))
  (let [file (:file params)
        s (slurp (:tempfile file))]
    (-> {:result :ok
         :md s}
      resp/ok)))
