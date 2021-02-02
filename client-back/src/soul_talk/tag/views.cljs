(ns soul-talk.tag.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du]
            [soul-talk.common.styles :as styles]
            ["@material-ui/core" :refer [Modal Form Input Row Col Button Table Divider]]
            ["@material-ui/icons" :refer [SearchOutlined EditOutlined DeleteOutlined]]))

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
                       :on-click #(dispatch [:tags/load-all (merge @params @pagination)])}
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
                                             (c/modal
                                               "删除"
                                               (str "你确认要删除吗？")
                                               #(dispatch [:tags/delete id])
                                               #(js/console.log "cancel"))))}]])))}])

(defn list-table []
  (r/with-let [tags (subscribe [:tags])]
    (fn []
      [:div.search-result-list
       [:> Table {:dataSource (clj->js @tags)
                  :columns    (clj->js list-columns)
                  :row-key    "id"
                  :bordered   true}]])))

(defn query-page [props]
  [c/layout props
   [:<>
    [query-form]
    [list-table]]])


(defn home []
  (styles/styled-layout query-page))

