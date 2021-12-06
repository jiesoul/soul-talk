(ns soul-talk.file.files
  (:require [cambium.core :as log]
            [ring.util.http-response :as resp]
            [clojure.java.io :as io]))

(defn upload-md! [{:keys [body params] :as req}]
  (log/info (:params req))
  (let [file (:file params)
        s (slurp (:tempfile file))]
    (-> {:result :ok
         :md s}
      resp/ok)))
