(ns soul-talk.series.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :series-list query)

(reg-sub :series query)