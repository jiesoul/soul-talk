(ns soul-talk.article.events
  (:require [re-frame.core :refer [reg-event-fx reg-event-db subscribe]]
            [ajax.core :refer [POST GET DELETE PUT]]
            [soul-talk.db :refer [api-uri]]
            [taoensso.timbre :as log]))

(reg-event-db
  :set-public-articles
  (fn [db [_ {:keys [articles pagination]}]]
    (assoc db :public-articles articles
              :home-pagination pagination)))

(reg-event-fx
  :load-public-articles
  (fn [_ [_ pagination]]
    (js/console.log "url:" api-uri)
    {:http {:method        GET
            :url           (str api-uri "/articles/public")
            :ajax-map      {:params pagination}
            :success-event [:set-public-articles]}}))

(reg-event-db
  :clear-public-articles
  (fn [db _]
    (dissoc db :public-articles)))

(reg-event-db
  :set-articles
  (fn [db [_ {:keys [articles pagination]}]]
    (assoc db :articles articles
              :edit-pagination pagination)))

(reg-event-fx
  :load-articles
  (fn [_ [_ pagination]]
    {:http {:method        GET
            :url           (str api-uri "/articles")
            :ajax-map      {:params pagination}
            :success-event [:set-articles]}}))

(reg-event-db
  :clear-articles
  (fn [db _]
    (dissoc db :all-articles)))

(reg-event-fx
  :articles/add-ok
  (fn [{:keys [db]} [_ {:keys [article]}]]
    {:dispatch-n (list [:dispatch (str "/articles/" (:id article) "/edit")])}))

(reg-event-fx
  :articles/add
  (fn [_ [_ article]]
    {:http {:method        POST
            :url           (str api-uri "/articles")
            :ajax-map      {:params article}
            :success-event [:articles/add-ok article]}}))

(reg-event-fx
  :articles/upload-ok
  (fn [{:keys [db]} [_ {:keys [id] :as article}]]
    {:db (-> db
           )
     :dispatch-n (list [:set-success "保存成功"])}))

(reg-event-db
  :articles/upload-error
  (fn [_ [_ {:keys [message]}]]
    (js/alert message)))

(reg-event-fx
  :articles/upload
  (fn [_ [_ files]]
    (let [data (doto
                 (js/FormData.)
                 (.append "file" files))]
      {:http
       {:method   POST
        :url               (str api-uri "/articles/upload")
        :ajax-map          {:body data}
        :success-event [:articles/upload-ok]
        :error-event [:articles/upload-error]}})))

(reg-event-fx
  :articles/edit-ok
  (fn [_ _]
    {:dispatch-n (list [:set-success "保存成功"]
                        [:admin/load-articles])}))

(reg-event-fx
  :articles/edit-error
  (fn [_ [_ {:keys [response]}]]
    {:dispatch [:set-error (:message response)]}))

(reg-event-fx
  :articles/edit
  (fn [_ [_ {:keys [id counter] :as article}]]
    {:http {:method        PUT
            :url           (str api-uri "/articles/" id)
            :ajax-map      {:params article}
            :success-event [:articles/edit-ok]
            :error-event   [:articles/edit-error]}}))

(reg-event-db
  :set-article
  (fn [db [_ {article :article}]]
    (assoc db :article article)))

(reg-event-fx
  :load-article
  (fn [_ [_ id]]
    {:http {:method        GET
            :url           (str api-uri "/articles/" id)
            :success-event [:set-article]}}))

(reg-event-db
  :clear-article
  (fn [db _]
    (dissoc db :article)))

(reg-event-fx
  :articles/delete-ok
  (fn [{:keys [db]} [_ id]]
    (let [articles (get db :admin/articles)
          articles (remove #(= id (:id %)) articles)]
      {:dispatch [:set-success "删除成功"]
       :db       (assoc db :admin/articles articles)})))

(reg-event-db
  :articles/delete-error
  (fn [_ _]
    (js/alert "delete fail")))

(reg-event-fx
  :articles/delete
  (fn [_ [_ id]]
    {:http {:method        DELETE
            :url           (str api-uri "/articles/" id)
            :success-event [:articles/delete-ok id]
            :error-event   [:articles/delete-error]}}))

(reg-event-fx
  :articles/publish-ok
  (fn [_ _]
    {:dispatch-n (list [:set-success "发布成功"]
                        [:admin/load-articles])}))

(reg-event-fx
  :articles/publish-error
  (fn [_ _]
    (js/alert "publish failed")))

(reg-event-fx
  :articles/publish
  (fn [_ [_ id]]
    {:http {:method PUT
            :url (str api-uri "/articles/" id "/publish")
            :success-event [:articles/publish-ok]
            :error-event [:articles/publish-error]}}))

(reg-event-db
  :set-public-articles-archives
  (fn [db [_ {:keys [archives]}]]
    (assoc db :public-articles-archives archives)))

(reg-event-fx
  :load-public-articles-archives
  (fn [_ _]
    {:http {:method        GET
            :url           (str api-uri "/articles/archives")
            :success-event [:set-public-articles-archives]}}))

(reg-event-db
  :set-public-articles-archives-year-month
  (fn [db [_ {:keys [articles]}]]
    (assoc db :public-articles-archives-year-month articles)))

(reg-event-fx
  :load-public-articles-archives-year-month
  (fn [_ [_ year month]]
    {:http {:method        GET
            :url           (str api-uri "/articles/archives/" year "/" month)
            :success-event [:set-public-articles-archives-year-month]}}))

(reg-event-db
  :upload-md-file-ok
  (fn [db [_ {:keys [md]}]]
    (.val (js/$ "#editMdTextarea") md)
    (assoc db :upload/md md)))

(reg-event-db
  :upload-md-file-error
  (fn [_ [_ {:keys [message]}]]
    (js/alert message)))

(reg-event-fx
  :upload-md-file
  (fn [_ [_ files]]
    (let [data (doto
                 (js/FormData.)
                 (.append "file" files))]
      {:http
       {:method   POST
        :url               (str api-uri "/admin/files/md")
        :ajax-map          {:body data}
        :success-event [:upload-md-file-ok]
        :error-event [:upload-md-file-error]}})))