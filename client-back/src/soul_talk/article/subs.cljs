(ns soul-talk.article.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :articles query)

(reg-sub :article query)

(reg-sub :editing-article query)
