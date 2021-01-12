(ns soul-talk.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]
            [soul-talk.site-info.subs]
            [soul-talk.auth.subs]
            [soul-talk.user.subs]
            [soul-talk.role.subs]
            [soul-talk.menu.subs]
            [soul-talk.app-key.subs]
            [soul-talk.collect-link.subs]
            [soul-talk.collect-site.subs]
            [soul-talk.series.subs]
            [soul-talk.data-dic.subs]
            [soul-talk.dash.subs]
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

(reg-sub :api-url query)

(reg-sub :app-key query)

(reg-sub :active-page query)

(reg-sub :error query)

(reg-sub :success query)

(reg-sub :login-events query)

(reg-sub :loading? query)

(reg-sub :pagination query)

(reg-sub :query-params query)

(reg-sub :drawer-open query)