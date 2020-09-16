(ns soul-talk.tag.interface
  (:require [soul-talk.tag.handler :as handler]
            [soul-talk.tag.spec :as spec]))

(defn insert-tag! [tag]
  (handler/insert-tag! tag))