(ns soul-talk.app
  (:require [devtools.core :as devtools]
            [soul-talk.core :as core]
            [re-frame.core :as rf]
            [district0x.re-frame.google-analytics-fx :as ga]))

;; disable Google analytics
(ga/set-enabled! false)
;(district0x.re-frame.google-analytics-fx/set-enabled! (not goog.DEBUG))

(enable-console-print!)

(set! *warn-on-infer* true)

(devtools/set-pref! :dont-detect-custom-formatters true)
(devtools/install!)

(rf/clear-subscription-cache!)

(core/init!)

