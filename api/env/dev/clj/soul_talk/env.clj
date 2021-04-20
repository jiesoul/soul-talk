(ns soul-talk.env
  (:require [clojure.tools.logging :as log]
            [soul-talk.dev-middleware :refer [wrap-dev]]
            [mount.core :refer [args defstate]]
            [cprop.core :refer [load-config]]
            [cprop.source :as source :refer [from-system-props from-env]]))

(def defaults
  {:init
   (fn []
     (log/info "====[System started successfully using the development profile]===="))
   :stop
   (fn []
     (log/info "====[System has shut down]====="))
   :middleware wrap-dev})