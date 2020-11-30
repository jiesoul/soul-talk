(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.utils :as du]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        ori-data-dic (subscribe [:data-dic])
        update-data-dic (-> @ori-data-dic
                     (update :name #(or % ""))
                     (update :create_by #(or % (:id @user)))
                     r/atom)
        name (r/cursor update-data-dic [:name])]
    (fn []
      [:> Modal {:visible    @*visible*
                 :title      "add a data-dic"
                 :okText     "Create"
                 :cancelText "Cancel"
                 :onCancel   #(do
                                (dispatch [:data-dices/clean-data-dic])
                                (reset! *visible* false))
                 :onOk       #(do
                                (dispatch [:data-dices/add @update-data-dic]))}
       [:> Form {:name "add_data-dic_form"}
        [:> Form.Item {:title "name"
                       :label "name"
                       :required true
                       :rules [{:require true :message "please enter name"}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (reset! name value))}
         [:> Input]]]])))

(defn query-form []
  (let [pagination (subscribe [:pagination])
        params (r/atom nil)
        name   (r/cursor params [:name])]
    (fn []
      [:div
       [:> Form {:title     ""
                 :className "advanced-search-form"}
        [:> Row {:gutter 24}
         [:> Col {:span 8}
          [:> Form.Item {:name  "name"
                         :label "name"}
           [:> Input {:placeholder "name"
                      :value       @name
                      :on-blur     #(reset! name (-> % .-target .-value))}]]]]
        [:> Row
         [:> Col {:span 24 :style {:text-align "right"}}
          [:div
           [:> Button {:type     "primary"
                       :htmlType "submit"
                       :on-click #(dispatch [:data-dices/load-all (merge @params @pagination)])}
            "search"]
           [:> Button {:type     "dashed" :style {:margin "0 8px"}
                       :on-click #(reset! *visible* true)}
            "new"]]]]]
       [edit-form]])))

(def list-columns
  [{:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title  "创建时间" :dataIndex "create_at" :key "create_at" :align "center"
    :render (fn [_ article]
              (let [article (js->clj article :keywordize-keys true)]
                (du/to-date-time (:create_at article))))}
   {:title  "更新时间" :dataIndex "update_at" :key "update_at" :align "center"
    :render (fn [_ article]
              (let [article (js->clj article :keywordize-keys true)]
                (du/to-date-time (:update_at article))))}
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
                                               #(dispatch [:data-dices/delete id])
                                               #(js/console.log "cancel"))))}]])))}])

(defn list-table []
  (r/with-let [data-dices (subscribe [:data-dices])]
    (fn []
      [:div.search-result-list
       [:> Table {:dataSource (clj->js @data-dices)
                  :columns    (clj->js list-columns)
                  :row-key    "id"
                  :bordered   true}]])))

(defn query-page
  []
  [c/manager-layout
   [:div
    [query-form]
    [list-table]]])
