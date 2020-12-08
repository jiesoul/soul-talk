(ns soul-talk.menu.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :menus query)

(reg-sub :menu query)

(reg-sub :menus/query-params query)
