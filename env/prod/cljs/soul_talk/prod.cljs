(ns soul-talk.prod
  (:require [soul-talk.core :as core]))

(set! *print-fn* (fn [& _]))

(core/init!)
