(ns soul-talk.events
  (:require [re-frame.core :refer [inject-cofx dispatch dispatch-sync reg-event-db reg-event-fx subscribe]]
            [soul-talk.db :refer [default-db]]
            [soul-talk.common.local-storage :as storage]
            [soul-talk.site-info.events]
            [soul-talk.auth.events]
            [soul-talk.user.events]
            [soul-talk.role.events]
            [soul-talk.menu.events]
            [soul-talk.app-key.events]
            [soul-talk.series.events]
            [soul-talk.data-dic.events]
            [soul-talk.dash.events]
            [soul-talk.article.events]
            [soul-talk.tag.events]))

;; 初始化
(reg-event-fx
  :initialize-db
  [(inject-cofx :local-store storage/login-user-key)]
  (fn [cofx _]
    (let [user (get-in cofx [:local-store storage/login-user-key])
          db (:db cofx)]
      {:db (merge db (assoc default-db :user (js->clj user :keywordize-keys true)))
       :dispatch-n [[:site-info/load 1]
                    [:data-dic/load-all]]})))

;; 设置当前页
(reg-event-db
  :set-active-page
  (fn [db [_ page]]
    (assoc db :active-page page)))

(reg-event-fx
  :navigate-to
  (fn [_ [_ url]]
    {:navigate url}))

(reg-event-db
  :set-dialog-status
  (fn [db [_ [key ^boolean? value]]]
    (assoc db key value)))

(reg-event-db
  :set-drawer-status
  (fn [db [_ value]]
    (assoc db :drawer-status value)))

(reg-event-db
  :clean-success
  (fn [db _]
    (dissoc db :success)))

(reg-event-fx
  :set-success
  (fn [cfx [_ message]]
    {:db (assoc (:db cfx) :success message)
     :timeout {:id "clean-success"
               :event [:clean-success]
               :time 3000}}))

(reg-event-fx
  :set-error
  (fn [cfx [_ message]]
    {:db (assoc (:db cfx) :error message)
     :timeout {:id "clean-error"
               :event [:clean-error]
               :time 10000}}))

(reg-event-db
  :clean-error
  (fn [db _]
    (dissoc db :error)))

(reg-event-db
  :update-value
  (fn [db [_ keys val]]
    (assoc-in db keys val)))

;; 取消加载
(reg-event-db
  :unset-loading
  (fn [db _]
    (dissoc db :loading? :should-be-loading?)))

;; 设置加载为 true
(reg-event-db
  :set-loading-for-real-this-time
  (fn [{:keys [should-be-loading?] :as db} _]
    (if should-be-loading?
      (assoc db :loading? true)
      db)))

;; 设置加载
(reg-event-fx
  :set-loading
  (fn [{db :db} _]
    {:dispatch-later [{:ms 100 :dispatch [:set-loading-for-real-this-time]}]
     :db             (-> db
                       (assoc :should-be-loading? true))}))





