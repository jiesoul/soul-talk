(ns soul-talk.app
  (:require [soul-talk.core :as core]
            [devtools.core :as devtools]
            [re-frame.core :as rf]
            [react]
            [moment]))

(enable-console-print!)

(set! *warn-on-infer* true)

(.log js/console react/Component)

(devtools/set-pref! :dont-detect-custom-formatters true)
(devtools/install!)

(rf/clear-subscription-cache!)
(goog-define api-uri "http://localhost:3000/api/v1")

(core/init!)

