(ns soul-talk.env
  (:require [taoensso.timbre :as log]))

(def defaults
  {:init
   (fn []
     (log/info "====[soul_talk started successfully using the development profile]===="))
   :stop
   (fn []
     (log/info "====[soul_talk has shut down]====="))})