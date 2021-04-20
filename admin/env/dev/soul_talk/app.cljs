(ns soul-talk.app
  (:require [devtools.core :as devtools]
            [soul-talk.core :as core]
            [re-frame.core :as rf]
            [district0x.re-frame.google-analytics-fx :as ga]))

(enable-console-print!)

(set! *warn-on-infer* true)

(devtools/set-pref! :dont-detect-custom-formatters true)
(devtools/install!)

(rf/clear-subscription-cache!)

(core/init!)

