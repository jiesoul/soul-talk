(ns soul-talk.app
  (:require [soul-talk.core :as core]))

(set! *print-fn* (fn [& _]))

(goog-define api-uri "http://jiesoul.com/api")
(core/init!)
