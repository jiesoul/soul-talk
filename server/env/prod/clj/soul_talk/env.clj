(ns soul-talk.env
  (:require [taoensso.timbre :as log]
            [mount.core :refer [args defstate]]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]))


(defstate conf :start (load-config :resources "prod/resources/config.edn"
                       :merge [(args)
                               (source/from-system-props)
                               (source/from-env)]))
(def defaults
  {:init
   (fn []
     (log/info "====[soul-talk started successfully]===="))
   :stop
   (fn []
     (log/info "====[soul-talk has shut down successfully]====="))
   :middleware identity})