(ns figwheel
  (:require [figwheel-sidecar.repl-api :as fa]))

(defn start-fw []
  (fa/start-figwheel!))

(defn stop-fw []
  (fa/stop-figwheel!))

(defn cljs []
  (fa/cljs-repl))
