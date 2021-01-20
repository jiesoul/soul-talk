(ns soul-talk.menu.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :menus query)
(reg-sub :menus/edit query)
(reg-sub :menus/query-params query)
(reg-sub :menus/pagination query)
(reg-sub :menus/add-status query)
(reg-sub :menus/edit-status query)
(reg-sub :menus/delete-status query)
(reg-sub :menus/selected query)
