(ns soul-talk.app
  (:require [soul-talk.core :as core]
            [district0x.re-frame.google-analytics-fx]))

(set! *print-fn* (fn [& _]))

(core/init!)
