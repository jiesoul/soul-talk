(ns soul-talk.auth.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :login-token query)

(reg-sub :auth-key/list query)

(reg-sub :auth-key/query-params query)

(reg-sub :auth-key/pagination query)

(reg-sub :auth-key/delete-dialog query)

(reg-sub :auth-key/edit query)