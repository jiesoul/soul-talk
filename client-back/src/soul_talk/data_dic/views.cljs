(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.utils :as utils]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])
        id (:id @data-dic)]
    (fn []
      [:> Modal {:visible    @*visible*
                 :title      (str (if id "编辑" "添加") "数据字典")
                 :okText     "保存"
                 :cancelText "退出"
                 :onCancel   #(do
                                (dispatch [:data-dices/clean-data-dic])
                                (reset! *visible* false))
                 :onOk       #(let [data-dic @data-dic]
                                (js/console.log "data-dic: " data-dic)
                                (assoc data-dic :update_by user-id)
                                (if id
                                  (dispatch [:data-dices/update data-dic])
                                  (dispatch [:data-dices/add (assoc data-dic :create_by user-id)])))}
       [:> Form {:name "add-data-dic-form"
                 :initial-values @data-dic
                 :validate-messages c/validate-messages
                 :labelCol {:span 8}
                 :wrapperCol {:span 8}}
        [:> Form.Item {:name "id"
                       :label "id"
                       :rules [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :id value]))}
         [:> Input ]]
        [:> Form.Item {:name "name"
                       :label "名称"
                       :rules [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :name value]))}
         [:> Input]]
        [:> Form.Item {:name "pid"
                       :label "父id"
                       :rules [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :pid value]))}
         [:> Input ]]
        [:> Form.Item {:name "note"
                       :label "备注"
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :note value]))}
         [:> Input ]]
        ]])))

(defn query-form []
  (let [pagination (subscribe [:pagination])
        params (subscribe [:data-dices/query-params])
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
                       :on-click #(dispatch [:data-dices/load-page (merge @params @pagination)])}
            "search"]
           [:> Button {:type     "dashed" :style {:margin "0 8px"}
                       :on-click #(reset! *visible* true)}
            "new"]]]]]
       ])))

(def list-columns
  [{:title "ID" :dataIndex "id", :key "id", :align "center"}
   {:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title "父ID" :dataIndex "pid", :key "pid", :align "center"}
   {:title "备注" :dataIndex "note", :key "note", :align "center"}
   {:title  "创建时间" :dataIndex "create_at" :key "create_at" :align "center"
    :render (fn [_ article]
              (let [article (js->clj article :keywordize-keys true)]
                (utils/to-date-time (:create_at article))))}
   {:title "创建人" :dataIndex "create_by", :key "create_by", :align "center"}
   {:title  "更新时间" :dataIndex "update_at" :key "update_at" :align "center"
    :render (fn [_ article]
              (let [article (js->clj article :keywordize-keys true)]
                (utils/to-date-time (:update_at article))))}
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
  (let [data-dices (subscribe [:data-dices])]
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
    [edit-form]
    [list-table]]])
