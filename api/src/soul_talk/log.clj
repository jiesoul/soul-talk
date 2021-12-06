(ns soul-talk.log
  (:require [taoensso.timbre :as timbre]
            [cheshire.core :as json]
            [clojure.spec.alpha :as s]))

(defn- json-output [{:keys [level msg_ instant]}]
  (let [event (read-string (force msg_))]
    (json/generate-string {:timestamp instant
                           :level level
                           :event event})))

;(defn init! []
;  (timbre/merge-config! {:output-fn json-output}))
;
;(defn write! [{:keys [level] :as event}] ; <1>
;  {:pre [(s/assert ::event/event event)]} ; <2>
;  (case level ; <3>
;    :info (timbre/info event)
;    :debug (timbre/debug event)
;    :warn (timbre/warn event)
;    :error (timbre/error event)
;    :fatal (timbre/fatal event)))