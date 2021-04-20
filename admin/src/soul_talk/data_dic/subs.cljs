(ns soul-talk.data-dic.subs
  (:require [re-frame.core :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :data-dices query)

(reg-sub :data-dic/list query)

(reg-sub :data-dic/pagination query)

(reg-sub :data-dic/query-params query)

(reg-sub :data-dic/edit query)

(reg-sub :data-dic/delete-dialog query)
