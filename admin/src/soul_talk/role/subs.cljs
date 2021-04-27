(ns soul-talk.role.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))

(reg-sub :role/delete-dialog query)
(reg-sub :role/query-params query)
(reg-sub :role/list query)
(reg-sub :role/pagination query)
(reg-sub :role/edit query)