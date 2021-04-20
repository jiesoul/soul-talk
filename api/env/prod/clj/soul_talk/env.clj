(ns soul-talk.env
  (:require [clojure.tools.logging :as log]
            [mount.core :refer [args defstate]]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]))

(def defaults
  {:init
   (fn []
     (log/info "====[soul-talk started successfully]===="))
   :stop
   (fn []
     (log/info "====[soul-talk has shut down successfully]====="))
   :middleware identity})