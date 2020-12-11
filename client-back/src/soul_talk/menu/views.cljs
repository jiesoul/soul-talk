(ns soul-talk.menu.views
  (:require [soul-talk.common.views :as c]
            [antd :as antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [taoensso.timbre :as log]))

(def ^:dynamic *edit-visible* (r/atom false))
(def ^:dynamic *add-visible* (r/atom false))

(defn edit-input []
  [:<>
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

   ])

(defn menu-form []
  (let [menu (subscribe [:menu])]
    (fn []
      [:> Form {:name              "add-menu-form"
                :validate-messages c/validate-messages
                :labelCol          {:span 8}
                :wrapperCol        {:span 8}
                :initial-values    @menu
                :preserve false}
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
        [:> Input]]])))

(defn add-form [menu user]
  (fn [menu user]
    (let [user-id (:id @user)
          this (r/current-component)]
      [c/modal {:visible  @*add-visible*
                :title    "添加菜单"
                :onCancel (fn [e props]
                            (js/console.log "========" e )
                            (js/console.log "----------" this )
                            (do
                                   (dispatch [:menus/clean-menu])
                                   (reset! *add-visible* false)))
                :onOk     #(let [menu @menu]
                             (assoc menu :update_by user-id)
                             (dispatch [:menus/add (assoc menu :create_by user-id)]))}
       [menu-form menu]
       ])))

(defn edit-form [menu user]
  (let [user-id  (:id @user)]
    (if @menu
      [:> Modal {:visible    @*edit-visible*
                 :title      "编辑数据字典"
                 :okText     "保存"
                 :cancelText "退出"
                 :onCancel   #(reset! *edit-visible* false)
                 :onOk       #(let [menu @menu]
                                (assoc menu :update_by user-id)
                                (dispatch [:menus/update menu]))}
       [:> Form {:name              "add-menu-form"
                 :initial-values    @menu
                 :validate-messages c/validate-messages
                 :labelCol          {:span 8}
                 :wrapperCol        {:span 8}}
        [:> Form.Item {:name  "id"
                       :label "id"
                       :rules [{:required true}]}
         [:> Input {:read-only true}]]
        [edit-input]
        ]])))

(defn query-form []
  (let [query-params (subscribe [:menus/query-params])
        this (r/current-component)
        children (r/children this)
        form         (fn []
                       (js/Form.userForm))]
    (fn []
      (let [{:keys [id name pid]} @query-params]
        (js/console.log "query-form reload++++++++++ query-param: " @query-params)
        [:> Form {:name      "query-form"
                  :className "advanced-search-form"}
         [:> Row {:gutter 24}
          [:> Col {:span 8}
           [:> Form.Item {:name      "id"
                          :label     "id"
                          :on-change #(dispatch [:menus/set-query-params :id (-> % .-target .-value)])}
            [:input {:value (:id @query-params)}]]]
          [:> Col {:span 8}
           [:> Form.Item {:name      "name"
                          :label     "name"
                          :on-change #(dispatch [:menus/set-query-params :name (-> % .-target .-value)])}
            [:input {:value (:name @query-params)}]]]
          [:> Col {:span 8}
           [:> Form.Item {:name      "pid"
                          :label     "父id"
                          :on-change #(dispatch [:menus/set-query-params :pid (-> % .-target .-value)])}
            [:input {:value (:pid @query-params)}]]]]
         [:> Row
          [:> Col {:span 24 :style {:text-align "right"}}
           [:> Form.Item
            [:> Button {:type     "primary"
                        :htmlType "reset"
                        :style    {:margin "0 8px"}
                        :on-click #(do
                                    (dispatch [:menus/clean-query-params])
                                    (let [form (-> % .-props)]
                                      (js/console.log "===Form: "form)
                                      (js/console.log "====this: " (-> this))))}
             "重置"]
            [:> Button {:type     "primary"
                        :htmlType "submit"
                        :style    {:margin "0 8px"}
                        :on-click #(dispatch [:menus/load-page @query-params])}
             "搜索"]
            [:> Button {:type     "dashed"
                        :style    {:margin "0 8px"}
                        :on-click (fn []
                                    (dispatch [:menus/clean-menu])
                                    (reset! *add-visible* true))}
             "新增"]]]]])))
  )

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

(defn list-table [menus]
  (fn [menus]
    [:div.search-result-list
     [:> Table {:dataSource (clj->js @menus)
                :columns    (clj->js list-columns)
                :row-key    "id"
                :bordered   true}]]))

(defn query-page
  []
  (let [user (subscribe [:user])
        query-params (subscribe [:menus/query-params])
        menus (subscribe [:menus])
        menu (subscribe [:menu])]
    [c/manager-layout
     [:div
      [add-form menu user]
      [query-form query-params]
      [edit-form menu user]
      [list-table menus]]]))