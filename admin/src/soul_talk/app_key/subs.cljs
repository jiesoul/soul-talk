(ns soul-talk.app-key.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))

(reg-sub :app-key/list query)
(reg-sub :app-key/query-params query)
(reg-sub :app-key/pagination query)
(reg-sub :app-key/delete-dialog query)
(reg-sub :app-key/edit query)