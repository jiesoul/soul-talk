(ns soul-talk.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.effects :refer [query]]
            [soul-talk.site-info.subs]
            [soul-talk.series.subs]
            [soul-talk.tag.subs]
            [soul-talk.article.subs]))

;; 获取当时全部数据
(reg-sub
  :db-state
  (fn [db _]
    db))

(reg-sub
  :initialised?
  (fn [db _]
    (not (empty? db))))

(reg-sub :active-page query)

(reg-sub :loading? query)

(reg-sub :pagination query)

(reg-sub :query-params query)