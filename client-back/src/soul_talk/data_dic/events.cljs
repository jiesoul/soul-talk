(ns soul-talk.data-dic.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :data-dices/load-all-ok
  (fn [db [_ {:keys [data-dices pagination]}]]
    (assoc db :data-dices data-dices :pagination pagination)))

(reg-event-fx
  :data-dices/load-all
  (fn [_ params]
    (js/console.log "query params: " params)
    {:http {:method        GET
            :url           (str site-uri "/data-dices")
            :ajax-map      {:params params}
            :success-event [:data-dices/load-all-ok]}}))

(reg-event-db
  :data-dices/add-ok
  (fn [db [_ {:keys [data-dic]}]]
    (js/console.log "body: " data-dic)
    (assoc db :success "add a data-dic ok")))

(reg-event-fx
  :data-dices/add
  (fn [_ [_ {:keys [name] :as data-dic}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/data-dices")
              :ajax-map      {:params data-dic}
              :success-event [:data-dices/add-ok]}})))

(reg-event-db
  :data-dices/delete-ok
  (fn [db [_ id]]
    (let [data-dices (:data-dices db)
          data-dices (remove #(= id (:id %)) data-dices)]
      (assoc db :success "删除成功" :data-dices data-dices))))

(reg-event-fx
  :data-dices/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/data-dices/" id)
            :success-event [:data-dices/delete-ok id]}}))

(reg-event-db
  :data-dices/clean-data-dic
  (fn [db _]
    (dissoc db :data-dic)))

