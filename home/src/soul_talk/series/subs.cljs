(ns soul-talk.series.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))

(reg-sub :series/query-params query)
(reg-sub :series/pagination query)
(reg-sub :series/list query)
(reg-sub :series/series query)
(reg-sub :series/edit query)
(reg-sub :series/new-dialog query)
