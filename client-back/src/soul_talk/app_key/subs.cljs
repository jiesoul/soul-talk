(ns soul-talk.app-key.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :tags query)

(reg-sub :tag query)