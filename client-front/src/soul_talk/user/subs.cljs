(ns soul-talk.user.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :auth-token query)

(reg-sub :user query)

(reg-sub :csrf-token query)

(reg-sub :users query)