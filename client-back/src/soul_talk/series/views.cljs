(ns soul-talk.series.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as du]
            [soul-talk.common.styles :as styles]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]))

(def ^:dynamic *visible* (r/atom false))

(defn edit-form []
  (let [user (subscribe [:user])
        ori-series (subscribe [:series/series])
        update-series (-> @ori-series
                     (update :name #(or % ""))
                     (update :create_by #(or % (:id @user)))
                     r/atom)
        id (r/cursor update-series [:id])
        name (r/cursor update-series [:name])]
    [:> Form {:name "add_tag_form"}
     [:> Form.Input {:title     "name"
                     :label     "name"
                     :required  true
                     :rules     [{:require true :message "please enter name"}]
                     :on-change #(let [value (-> % .-target .-value)]
                                   (reset! name value))}]]))

(defn query-form []
  (let [params (r/atom {})
        name (r/cursor params [:name])]
    [:div
     [:> Form {:title     ""}
      [:> Form.group
       [:> Form.Input {:label       "名称"
                      :placeholder "name"
                      :on-blur     #(reset! name (-> % .-target .-value))}]]
      [:div {:style {:text-align "center"}}
       [:> Button {:variant  "outlined"
                   :color    "primary"
                   :size     "small"
                   :on-click #(dispatch [:series/load-page @params])}
        "查询"]
       [:> Button {:variant  "outlined"
                   :size     "small"
                   :style    {:margin "0 8px"}
                   :on-click #(dispatch [:series/set-add-dialog true])}
        "新增"]]]]))

(defn list-table [{:keys [classes]}]
  (let [series-list (subscribe [:series/list])
        query-params (subscribe [:series/query-params])
        pagination (subscribe [:series/pagination])]
    [:<>
     [:> Table {:sticky-header true
                    :aria-label    "list-table"
                    :size          "small"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.Cell {:align "center"} "序号"]
        [:> Table.Cell {:align "center"} "名称"]
        [:> Table.Cell {:align "center"} "简介"]
        [:> Table.Cell {:align "center"} "创建时间"]
        [:> Table.Cell {:align "center"} "更新时间"]
        [:> Table.Cell {:align "center"} "操作"]]]]
     [:> Table.Body
      (doall
        (for [{:keys [id name description create_at update_at] :as series} @series-list]
          ^{:key series}
          [:> Table.Row
           [:> Table.Cell {:align "center"} 1]
           [:> Table.Cell {:align "center"} name]
           [:> Table.Cell {:align "center"} description]
           [:> Table.Cell {:align "center"} (du/to-date-time create_at)]
           [:> Table.Cell {:align "center"} (du/to-date-time update_at)]
           [:> Table.Cell {:align "center"}
            [:div
             [:> Button {:type     "primary"
                                 :size     "small"
                                 :alt      "修改"
                         :icon "修改"
                                 :on-click (fn []
                                             (do
                                               (dispatch [:series/load id])
                                               (set! *visible* true)))}]
             [:> Button {:type     "danger"
                                 :size     "small"
                                 :alt      "删除"
                         :icon "delete"
                                 :on-click (fn []
                                             (r/as-element
                                               (c/modal
                                                 "删除"
                                                 (str "你确认要删除吗？")
                                                 #(dispatch [:series/delete id])
                                                 #(js/console.log "cancel"))))}]]]]))]]))

(defn home []
  [c/layout
   [:<>
    [query-form]
    [list-table]]])