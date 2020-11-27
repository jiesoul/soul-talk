(ns soul-talk.app-key.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :refer [EditOutlined DeleteOutlined]]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        app-key (r/atom {})]
    (fn []
      (let [name (r/cursor app-key [:name])]
        [:> Modal {:visible    @*visible*
                   :title      "add a app-key"
                   :okText     "Create"
                   :cancelText "Cancel"
                   :onCancel   #(do
                                  (reset! *visible* false)
                                  (reset! app-key {}))
                   :onOk       #(do
                                  (dispatch [:app-keys/add (assoc @app-key :create_by (:id @user))]))}
         [:> Form {:name "add_app-key_form"}
          [:> Form.Item {:title "name"
                         :label "name"
                         :rules [{:require true :message "please enter name"}]}
           [:> Input {:on-blur #(reset! name (-> % .-target .-value))}]]]]))))

(defn query-form []
  (let [pagination (subscribe [:pagination])
        params (r/atom nil)
        name (r/cursor params [:name])]
    [:div
     [:> Form {:title     ""
               :className "advanced-search-form"}
      [:> Row {:gutter 24}
       [:> Col {:span 8}
        [:> Form.Item {:name  "name"
                       :label "name"}
         [:> Input {:placeholder "name"
                    :on-blur     #(reset! name (-> % .-target .-value))}]]]]
      [:> Row
       [:> Col {:span 24 :style {:text-align "right"}}
        [:<>
         [:> Button {:type     "primary"
                     :htmlType "submit"
                     :on-click #(dispatch [:app-keys/load-all (merge @params @pagination)])}
          "search"]
         [:> Button {:style    {:margin "0 8px"}
                     :on-click #(reset! params nil)}
          "clear"]
         [:> Button {:type     "dashed" :style {:margin "0 8px"}
                     :on-click #(reset! *visible* true)}
          "new"]]
        ]]]
     [edit-form]
     ]))

(def list-columns
  [{:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"
    :render (fn [_ article]
              (r/as-element
                (let [{:keys [id]} (js->clj article :keywordize-keys true)]
                  [:div
                   [:> Button {:type     "danger"
                               :icon     (r/as-element [:> DeleteOutlined])
                               :size     "small"
                               :on-click (fn []
                                           (r/as-element
                                             (c/show-confirm
                                               "删除"
                                               (str "你确认要删除吗？")
                                               #(dispatch [:app-keys/delete id])
                                               #(js/console.log "cancel"))))}]])))}])

(defn app-keys-list []
  (r/with-let [app-keys (subscribe [:app-keys])]
    [:div.search-result-list
     [:> Table {:dataSource (clj->js @app-keys)
                :columns (clj->js list-columns)
                :row-key "id"
                :bordered true}]]))

(defn app-keys-page []
  [c/manager-layout
   [:div
    [query-form]
    [app-keys-list]]])




