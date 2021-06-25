(ns soul-talk.category.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))

(reg-sub :category/query-params query)
(reg-sub :category/pagination query)
(reg-sub :category/list query)
(reg-sub :category/series query)
(reg-sub :category/edit query)
(reg-sub :category/delete-dialog query)
