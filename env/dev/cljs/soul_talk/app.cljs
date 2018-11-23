(ns ^:figwheel-no-load soul-talk.app
  (:require [soul-talk.core :as core]
            [devtools.core :as devtools]
            [re-frame.core :as rf]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)
(rf/clear-subscription-cache!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :on-reload core/mount-component)

(devtools.core/set-pref! :dont-detect-custom-formatters true)
(devtools/install!)

(core/init!)
