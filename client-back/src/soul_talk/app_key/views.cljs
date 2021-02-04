(ns soul-talk.app-key.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du]
            [soul-talk.common.styles :as styles]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        app-key (r/atom {})]
    (fn []
      (let [name (r/cursor app-key [:name])]
        [:> Form {:name "add_app-key_form"}
         [:> Form.Input {:title "name"
                        :label "name"
                        :rules [{:require true :message "please enter name"}]}
          [:> Input {:on-blur #(reset! name (-> % .-target .-value))}]]]))))

(defn edit []
  [c/layout [edit-form]])

(defn query-form []
  (let [pagination (subscribe [:pagination])
        params (r/atom nil)
        name (r/cursor params [:name])]
    [:div
     [:> Form {:title     ""
               :className "advanced-search-form"}
      [:> Form.Group
       [:> Form.Input {:placeholder "name"
                  :on-blur     #(reset! name (-> % .-target .-value))}]]
      [:div
       [:<>
        [:> Button {:type     "primary"
                    :htmlType "submit"
                    :on-click #(dispatch [:app-keys/load-all (merge @params @pagination)])}
         "search"]
        [:> Button {:type     "dashed" :style {:margin "0 8px"}
                    :on-click #(reset! *visible* true)}
         "new"]]
       ]]]))

(def list-columns
  [{:title "app" :dataIndex "app_name", :key "app_name", :align "center"}
   {:title "token" :dataIndex "token" :key "token" :align "center"}
   {:title  "创建时间" :dataIndex "create_at" :key "create_at" :align "center"
    :render (fn [_ app-key]
              (let [app-key (js->clj app-key :keywordize-keys true)]
                (du/to-date-time (:create_at app-key))))}
   {:title  "更新时间" :dataIndex "refresh_at" :key "refresh_at" :align "center"
    :render (fn [_ app-key]
              (let [app-key (js->clj app-key :keywordize-keys true)]
                (du/to-date-time (:refresh_at app-key))))}
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"}])

(defn query-list []
  (r/with-let [app-keys (subscribe [:app-keys])]
    [:div.search-result-list
     [:> Table {:dataSource (clj->js @app-keys)
                :columns (clj->js list-columns)
                :row-key "id"
                :bordered true}]]))

(defn home []
  [c/layout
   [:div
    [query-form]
    [query-list]]])




