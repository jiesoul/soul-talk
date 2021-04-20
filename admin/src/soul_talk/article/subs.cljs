(ns soul-talk.article.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :article/list query)

(reg-sub :article/edit query)

(reg-sub :article/query-params query)

(reg-sub :article/pagination query)

(reg-sub :article/delete-dialog query)

(reg-sub :article/publish-dialog query)
