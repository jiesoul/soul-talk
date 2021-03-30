(ns soul-talk.user.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :auth-token query)

(reg-sub :user query)

(reg-sub :csrf-token query)

(reg-sub :user/roles query)
(reg-sub :user/edit query)
(reg-sub :user/list query)
(reg-sub :user/query-params query)
(reg-sub :user/pagination query)
(reg-sub :user/delete-dialog query)