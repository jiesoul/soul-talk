(ns soul-talk.menu.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))
(reg-sub :menus query)
(reg-sub :menu/list query)
(reg-sub :menu/edit query)
(reg-sub :menu/query-params query)
(reg-sub :menu/pagination query)
(reg-sub :menu/delete-dialog query)
(reg-sub :menu/selected query)
