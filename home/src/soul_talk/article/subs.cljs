(ns soul-talk.article.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.effects :refer [query]]))

(reg-sub :articles query)

(reg-sub :articles/edit query)

(reg-sub :articles/query-params query)

(reg-sub :articles/pagination query)

(reg-sub :articles/delete-dialog-open query)

(reg-sub :articles/publish-dialog query)
