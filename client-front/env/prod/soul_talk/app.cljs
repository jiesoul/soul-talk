(ns soul-talk.app
  (:require [soul-talk.core :as core]
            [district0x.re-frame.google-analytics-fx]))

(set! *print-fn* (fn [& _]))

;; enable Google analytics
;(district0x.re-frame.google-analytics-fx/set-enabled! false)
(district0x.re-frame.google-analytics-fx/set-enabled! (not goog.DEBUG))

(core/init!)
