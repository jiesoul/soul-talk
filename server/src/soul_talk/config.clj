(ns soul-talk.config
  (:require [mount.core :refer [args defstate]]
            [cprop.core :refer [load-config]]))

(defstate conf :start (load-config :resources "config.edn"
                        :merge [(args)]))
