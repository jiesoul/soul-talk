(ns soul-talk.site-info.subs
  (:require [re-frame.core :refer [reg-sub]]
     [soul-talk.effects :refer [query]]))

(reg-sub :site-info query)