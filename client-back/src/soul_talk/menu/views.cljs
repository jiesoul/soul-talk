(ns soul-talk.menu.views
  (:require [soul-talk.common.views :as c]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]))

(def ^:dynamic *edit-visible* (r/atom false))
(def ^:dynamic *add-visible* (r/atom false))

(defn add-form []
  (let [user-id  (:id @(subscribe [:user]))
        menu (subscribe [:menu])]
    (fn []
      [:> Modal {:visible    @*add-visible*
                 :title      "添加菜单"
                 :okText     "保存"
                 :cancelText "退出"
                 :onCancel   #(do
                                (reset! *add-visible* false))
                 :onOk       #(let [menu @menu]
                                (assoc menu :update_by user-id)
                                (dispatch [:menus/add (assoc menu :create_by user-id)]))}
       [:> Form {:name              "add-menu-form"
                 :validate-messages c/validate-messages
                 :labelCol          {:span 8}
                 :wrapperCol        {:span 8}}
        [:> Form.Item {:name      "id"
                       :label     "id"
                       :rules     [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:menus/set-attr :id value]))}
         [:> Input]]
        [:> Form.Item {:name      "name"
                       :label     "名称"
                       :rules     [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:menus/set-attr :name value]))}
         [:> Input]]
        [:> Form.Item {:name      "url"
                       :label     "地址"
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:menus/set-attr :name value]))}
         [:> Input]]
        [:> Form.Item {:name      "pid"
                       :label     "父id"
                       :rules     [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:menus/set-attr :pid value]))}
         [:> Input]]
        [:> Form.Item {:name      "note"
                       :label     "备注"
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:menus/set-attr :note value]))}
         [:> Input]]
        ]])))

(defn edit-form []
  (let [user-id  (:id @(subscribe [:user]))
        menu (subscribe [:menu])]
    (fn []
      (if @menu
        [:> Modal {:visible    @*edit-visible*
                   :title      "编辑数据字典"
                   :okText     "保存"
                   :cancelText "退出"
                   :onCancel   #(reset! *edit-visible* false)
                   :onOk #(let [menu @menu]
                            (assoc menu :update_by user-id)
                            (dispatch [:menus/update menu]))}
         [:> Form {:name              "add-menu-form"
                   :initial-values    @menu
                   :validate-messages c/validate-messages
                   :labelCol          {:span 8}
                   :wrapperCol        {:span 8}}
          [:> Form.Item {:name      "id"
                         :label     "id"
                         :rules     [{:required true}]}
           [:> Input {:disabled true}]]
          [:> Form.Item {:name      "name"
                         :label     "名称"
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :name value]))}
           [:> Input]]
          [:> Form.Item {:name      "url"
                         :label     "地址"
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :name value]))}
           [:> Input]]
          [:> Form.Item {:name      "pid"
                         :label     "父id"
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :pid value]))}
           [:> Input]]
          [:> Form.Item {:name      "note"
                         :label     "备注"
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:menus/set-attr :note value]))}
           [:> Input]]
          ]]))))

(defn query-form []
  (let [pagination (subscribe [:pagination])
        query-params (subscribe [:menus/query-params])
        this (r/current-component)]
    (fn []
      [:> Form {:name           "query-form"
                :className      "advanced-search-form"
                :initial-values @query-params}
       [:> Row {:gutter 24}
        [:> Col {:span 8}
         [:> Form.Item {:name      "id"
                        :label     "id"
                        :on-change #(dispatch [:menus/set-query-params :id (-> % .-target .-value)])}
          [:> Input]]]
        [:> Col {:span 8}
         [:> Form.Item {:name      "name"
                        :label     "name"
                        :on-change #(dispatch [:menus/set-query-params :name (-> % .-target .-value)])}
          [:> Input]]]
        [:> Col {:span 8}
         [:> Form.Item {:name      "pid"
                        :label     "父id"
                        :on-change #(dispatch [:menus/set-query-params :pid (-> % .-target .-value)])}
          [:> Input]]]]
       [:> Row
        [:> Col {:span 24 :style {:text-align "right"}}
         [:div
          [:> Button {:type     "primary"
                      :htmlType "submit"
                      :size     "small"
                      :style    {:margin "0 8px"}
                      :on-click #(let []
                                   (js/console.log "this: " this)
                                   (js/console.log "this props: " (r/props this))
                                   (js/console.log "this children: " (r/children this)))}
           "重置"]
          [:> Button {:type     "primary"
                      :htmlType "submit"
                      :size     "small"
                      :style    {:margin "0 8px"}
                      :on-click #(dispatch [:menus/load-page @query-params])}
           "搜索"]
          [:> Button {:type     "dashed"
                      :style    {:margin "0 8px"}
                      :size     "small"
                      :on-click #(reset! *add-visible* true)}
           "新增"]]]]]
      )))

(def list-columns
  [{:title "ID" :dataIndex "id", :key "id", :align "center"}
   {:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title "地址" :dataIndex "url", :key "url", :align "center"}
   {:title "父ID" :dataIndex "pid", :key "pid", :align "center"}
   {:title "备注" :dataIndex "note", :key "note", :align "center"}
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"
    :render (fn [_ article]
              (r/as-element
                (let [{:keys [id]} (js->clj article :keywordize-keys true)]
                  [:div
                   [:> Button {:type "primary"
                               :icon (r/as-element [:> EditOutlined])
                               :size "small"
                               :on-click (fn []
                                           (dispatch [:menus/load-menu id])
                                           (reset! *edit-visible* true))}]

                   [:> Button {:type     "danger"
                               :icon     (r/as-element [:> DeleteOutlined])
                               :size     "small"
                               :style {:margin "0 8px"}
                               :on-click (fn []
                                           (r/as-element
                                             (c/show-confirm
                                               "删除"
                                               (str "你确认要删除吗？")
                                               #(dispatch [:menus/delete id])
                                               #(js/console.log "cancel"))))}]])))}])

(defn list-table []
  (let [menus (subscribe [:menus])]
    (fn []
      [:div.search-result-list
       [:> Table {:dataSource (clj->js @menus)
                  :columns    (clj->js list-columns)
                  :row-key    "id"
                  :bordered   true}]])))

(defn query-page
  []
  [c/manager-layout
   [:div
    [add-form]
    [query-form]
    [edit-form]
    [list-table]]])