(ns soul-talk.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :refer [from-env]]
            [mount.core :refer [args defstate]]))

(defstate conf :start (load-config :resources "config.edn"
                        :merge [(from-env)
                                (args)]))
