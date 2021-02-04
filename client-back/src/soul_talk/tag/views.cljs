(ns soul-talk.tag.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du]
            [soul-talk.common.styles :as styles]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        ori-tag (subscribe [:tag])
        update-tag (-> @ori-tag
                     (update :name #(or % ""))
                     (update :create_by #(or % (:id @user)))
                     r/atom)
        name (r/cursor update-tag [:name])]
    [:> Form {:name "add_tag_form"}
     [:> Form.Input {:title     "name"
                     :label     "name"
                     :required  true
                     :rules     [{:require true :message "please enter name"}]
                     :on-change #(let [value (-> % .-target .-value)]
                                   (reset! name value))}]]))

(defn edit []
  [c/layout [edit-form]])

(defn query-form []
  (let [pagination (subscribe [:pagination])
        params (r/atom nil)
        name   (r/cursor params [:name])]
    (fn []
      [:div
       [:> Form {:title     ""
                 :className "advanced-search-form"}
        [:> Form.Group
         [:> Form.Input {:placeholder "name"
                         :value       @name
                         :on-blur     #(reset! name (-> % .-target .-value))}]]
        [:div {:span 24 :style {:text-align "right"}}
         [:> Button {:type     "primary"
                     :htmlType "submit"
                     :on-click #(dispatch [:tags/load-all (merge @params @pagination)])}
          "search"]
         [:> Button {:type     "dashed" :style {:margin "0 8px"}
                     :on-click #(reset! *visible* true)}
          "new"]]]])))

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
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"}])

(defn list-table []
  (r/with-let [tags (subscribe [:tags])]
    (fn []
      [:div.search-result-list
       [:> Table {:dataSource (clj->js @tags)
                  :columns    (clj->js list-columns)
                  :row-key    "id"
                  :bordered   true}]])))

(defn home []
  [c/layout
   [:<>
    [query-form]
    [list-table]]])
