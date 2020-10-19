(ns soul-talk.app
  (:require [soul-talk.core :as core]
            [devtools.core :as devtools]
            [re-frame.core :as rf]
            [moment]))

(enable-console-print!)

(set! *warn-on-infer* true)

(js/console.log moment)

(devtools/set-pref! :dont-detect-custom-formatters true)
(devtools/install!)

(rf/clear-subscription-cache!)
(goog-define api-uri "http://localhost:3001/api/v1")

(core/init!)

