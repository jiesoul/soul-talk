(ns soul-talk.series.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :refer [EditOutlined DeleteOutlined SearchOutlined]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as du]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        ori-series (subscribe [:series])
        update-series (-> @ori-series
                     (update :name #(or % ""))
                     (update :create_by #(or % (:id @user)))
                     r/atom)
        id (r/cursor update-series [:id])
        name (r/cursor update-series [:name])]
    (fn []
      [:> Modal {:visible    @*visible*
                 :title      "添加系列"
                 :okText     "保存"
                 :cancelText "退出"
                 :onCancel   #(do
                                (dispatch [:tags/clean-tag])
                                (reset! *visible* false))
                 :onOk       #(if @id
                                (dispatch [:series/add @update-series])
                                (dispatch [:series/update @update-series]))}
       [:> Form {:name "add_tag_form"}
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
       [edit-form]
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
                       :size "middle"
                       :icon (r/as-element [:> SearchOutlined])
                       :on-click #(dispatch [:series/load-all (merge @params @pagination)])}
            "查询"]
           [:> Button {:type     "dashed"
                       :size "middle"
                       :style {:margin "0 8px"}
                       :on-click #(reset! *visible* true)}
            "新增"]]]]]])))

(def list-columns
  [{:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title "简介" :dataIndex "description" :key "description" :align "center"}
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
                   [:> Button {:type "primary"
                               :icon (r/as-element [:> EditOutlined])
                               :size "small"
                               :alt "修改"
                               :on-click (fn []
                                           (do
                                             (dispatch [:series/load id])
                                             (set! *visible* true)))}]
                   [:> Divider {:type "vertical"}]
                   [:> Button {:type     "danger"
                               :icon     (r/as-element [:> DeleteOutlined])
                               :size     "small"
                               :alt "删除"
                               :on-click (fn []
                                           (r/as-element
                                             (c/show-confirm
                                               "删除"
                                               (str "你确认要删除吗？")
                                               #(dispatch [:series/delete id])
                                               #(js/console.log "cancel"))))}]])))}])

(defn list-table []
  (r/with-let [series-list (subscribe [:series-list])]
    (fn []
      [:div.search-result-list
       [:> Table {:dataSource (clj->js @series-list)
                  :columns    (clj->js list-columns)
                  :row-key    "id"
                  :bordered   true}]])))

(defn query-page
  []
  [c/manager-layout
   [:div
    [query-form]
    [list-table]]])