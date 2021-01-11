(ns soul-talk.role.subs
  (:require [re-frame.core :as rf :refer [reg-sub]]
            [soul-talk.common.effects :refer [query]]))

(reg-sub :roles/add-dialog-open query)
(reg-sub :roles/edit-dialog-open query)
(reg-sub :roles/delete-dialog-open query)
(reg-sub :roles/menus-dialog-open query)
(reg-sub :roles/query-params query)
(reg-sub :roles/list query)
(reg-sub :roles/pagination query)
(reg-sub :roles/role query)
(reg-sub :roles/role-menus query)