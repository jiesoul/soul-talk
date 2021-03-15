(ns soul-talk.user.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :auth-token query)

(reg-sub :user query)

(reg-sub :csrf-token query)

(reg-sub :users query)

(reg-sub :user-roles query)

(reg-sub :users/list query)
(reg-sub :users/query-params query)
(reg-sub :users/pagination query)
(reg-sub :users/user query)
(reg-sub :users/user-roles query)
(reg-sub :users/roles-dialog-open query)
(reg-sub :users/new-dialog-open query)
(reg-sub :users/edit-dialog-open query)
(reg-sub :users/delete-dialog-open query)
(reg-sub :users/clean-query-params query)