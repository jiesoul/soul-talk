(ns soul-talk.tag.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :tag/list query)

(reg-sub :tag/edit query)

(reg-sub :tag/delete-dialog query)

(reg-sub :tag/query-params query)

(reg-sub :tag/pagination query)

