(ns soul-talk.menu.subs
  (:require [re-frame.core :as rf]
            [soul-talk.common.effects :refer [query]]))

(rf/reg-sub :menus query)

(rf/reg-sub :menu query)

(rf/reg-sub :menus/query-params)

(rf/reg-sub :menus/pagination)
