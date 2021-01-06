(ns soul-talk.role.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :roles query)

(reg-sub :role query)

(reg-sub :roles/query-params query)

(reg-sub :roles/selected query)

(reg-sub :roles/menus query)

(reg-sub :roles/role-menus query)