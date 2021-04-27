(ns soul-talk.tag.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))

(reg-sub :tags query)

(reg-sub :tag query)

