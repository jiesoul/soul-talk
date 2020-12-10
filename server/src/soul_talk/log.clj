(ns soul-talk.log
  (:require [clojure.tools.logging :as timbre]
            [cheshire.core :as json]))

(defn- json-output [{:keys [level msg_ instant]}]
  (let [event (read-string (force msg_))]
    (json/generate-string {:timestamp instant
                           :level level
                           :event event})))
