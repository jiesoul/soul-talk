(ns soul-talk.tag.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        ori-tag (subscribe [:tag])
        update-tag (-> @ori-tag
                     (update :name #(or % ""))
                     (update :create_by #(or % (:id @user)))
                     r/atom)
        name (r/cursor update-tag [:name])]
    (fn []
      [:> Modal {:visible    @*visible*
                 :title      "add a tag"
                 :okText     "Create"
                 :cancelText "Cancel"
                 :onCancel   #(do
                                (dispatch [:tags/clean-tag])
                                (reset! *visible* false))
                 :onOk       #(do
                                (dispatch [:tags/add @update-tag]))}
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
        params (r/atom {:name ""})
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
                       :on-click #(dispatch [:tags/load-all (merge @params @pagination)])}
            "search"]
           [:> Button {:style    {:margin "0 8px"}
                       :html-type "reset"
                       :on-click #(do
                                    (reset! params nil)
                                    (js/console.log "name: " @name))}
            "clear"]
           [:> Button {:type     "dashed" :style {:margin "0 8px"}
                       :on-click #(reset! *visible* true)}
            "new"]]
          ]]]
       [edit-form]
       ])))

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
                                               #(dispatch [:tags/delete id])
                                               #(js/console.log "cancel"))))}]])))}])

(defn tags-list []
  (r/with-let [tags (subscribe [:tags])]
    [:div.search-result-list
     [:> Table {:dataSource (clj->js @tags)
                :columns (clj->js list-columns)
                :row-key "id"
                :bordered true}]]))

(defn tags-page []
  [c/manager-layout
   [:<>
    [query-form]
    [tags-list]]])




