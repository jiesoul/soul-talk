(ns soul-talk.app-key.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :app-keys query)

(reg-sub :app-key query)