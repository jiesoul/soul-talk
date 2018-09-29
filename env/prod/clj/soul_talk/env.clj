(ns soul-talk.env
  (:require [taoensso.timbre :as log]))


(def defaults
  {:init
   (fn []
     (log/info "====[soul-talk started successfully]===="))
   :stop
   (fn []
     (log/info "====[soul-talk has shut down successfully]====="))})