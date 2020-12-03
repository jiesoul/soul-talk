(ns soul-talk.menu.subs
  (:require [re-frame.core :as rf]
            [soul-talk.common.effects :refer [query]]))

(rf/reg-sub :menus query)
