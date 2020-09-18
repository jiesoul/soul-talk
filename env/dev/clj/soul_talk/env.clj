(ns soul-talk.env
  (:require [taoensso.timbre :as log]
            [soul-talk.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "====[System started successfully using the development profile]===="))
   :stop
   (fn []
     (log/info "====[System has shut down]====="))
   :middleware wrap-dev})