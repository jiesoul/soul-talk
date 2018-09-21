(ns soul-talk.config
  (:require [mount.core :refer [defstate]]
            [cprop.core :refer [load-config]]
            [cprop.source :as source]))

(defstate conf
  :start (load-config))

