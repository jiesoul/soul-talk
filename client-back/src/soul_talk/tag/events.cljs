(ns soul-talk.tag.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]
            [ajax.core :refer [GET POST DELETE PUT]]
            [clojure.string :as str]
            [soul-talk.db :refer [site-uri]]))

(reg-event-db
  :tags/load-all-ok
  (fn [db [_ {:keys [tags pagination]}]]
    (assoc db :tags tags :pagination pagination)))

(reg-event-fx
  :tags/load-all
  (fn [_ params]
    (js/console.log "query params: " params)
    {:http {:method        GET
            :url           (str site-uri "/tags")
            :ajax-map      {:params params}
            :success-event [:tags/load-all-ok]}}))

(reg-event-db
  :tags/add-ok
  (fn [db [_ {:keys [tag]}]]
    (js/console.log "body: " tag)
    (assoc db :success "add a tag ok")))

(reg-event-fx
  :tags/add
  (fn [_ [_ {:keys [name] :as tag}]]
    (if (str/blank? name)
      {:dispatch [:set-error "名称不能为空"]}
      {:http {:method        POST
              :url           (str site-uri "/tags")
              :ajax-map      {:params tag}
              :success-event [:tags/add-ok]}})))

(reg-event-db
  :tags/delete-ok
  (fn [db [_ id]]
    (let [tags (:tags db)
          tags (remove #(= id (:id %)) tags)]
      (assoc db :success "删除成功" :tags tags))))

(reg-event-fx
  :tags/delete
  (fn [_ [_ id]]
    {:http {:method  DELETE
            :url (str site-uri "/tags/" id)
            :success-event [:tags/delete-ok id]}}))

(reg-event-db
  :tags/clean-tag
  (fn [db _]
    (dissoc db :tag)))
