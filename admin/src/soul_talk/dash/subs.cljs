(ns soul-talk.dash.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :edit-pagination query)

(reg-sub :breadcrumb query)