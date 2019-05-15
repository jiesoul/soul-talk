(ns soul-talk.env
  (:require [taoensso.timbre :as log]
            [selmer.parser :as parser]
            [soul-talk.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "====[soul_talk started successfully using the development profile]===="))
   :stop
   (fn []
     (log/info "====[soul_talk has shut down]====="))
   :middleware wrap-dev})