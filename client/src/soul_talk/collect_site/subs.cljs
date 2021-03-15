(ns soul-talk.collect-site.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :collect-sites query)

(reg-sub :collect-site query)
