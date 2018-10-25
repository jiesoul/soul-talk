(ns soul-talk.routes.files
  (:require [soul-talk.routes.common :refer [handler]]
            [taoensso.timbre :as log]))

(handler upload-md! [req]
  (log/info req))
