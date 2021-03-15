(ns soul-talk.collect-link.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :collect-links query)

(reg-sub :collect-link query)