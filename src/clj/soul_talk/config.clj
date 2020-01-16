(ns soul-talk.config
  (:require [mount.core :refer [args defstate]]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]))

(defstate env :start (load-config
                       :merge
                       [(args)
                        (source/from-system-props)
                        (source/from-env)]))

