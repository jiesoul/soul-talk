(ns soul-talk.series.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as du]
            [soul-talk.routes :refer [navigate!]]
            ["semantic-ui-react" :refer [Form Button Table Divider Container]]))

(def ^:dynamic *visible* (r/atom false))

(defn new []
  (let [user (subscribe [:user])
        user-id (:id @user)
        series (subscribe [:series/edit])
        _ (dispatch [:series/set-attr {:create_by user-id :update_by user-id}])]
    [c/layout
     [:> Form
      [:> Form.Input {:title     "name"
                      :label     "名称"
                      :required  true
                      :on-change #(dispatch [:series/set-attr {:name (du/event-value %)}])}]
      [:> Form.Input {:label     "简介"
                      :on-change #(dispatch [:series/set-attr {:description (du/event-value %)}])}]

      [:> Button.Group {:size "mini"}
       [:> Button {:content "返回"
                   :on-click #(js/history.go -1)}]
       [:> Button.Or]
       [:> Button {:content "保存"
                   :positive true
                   :on-click #(dispatch [:series/new @series])}]]]]))

(defn edit []
  (let [user (subscribe [:user])
        ori-series (subscribe [:series/series])
        update-series (-> @ori-series
                     (update :name #(or % ""))
                     (update :create_by #(or % (:id @user)))
                     r/atom)
        id (r/cursor update-series [:id])
        name (r/cursor update-series [:name])]
    [c/layout
     [:> Form
      [:> Form.Input {:title     "name"
                      :label     "name"
                      :required  true
                      :on-change #(let [value (-> % .-target .-value)]
                                    (reset! name value))}]]]))

(defn query-form []
  (let [params (subscribe [:series/query-params])
        name (r/cursor params [:name])]
    [:> Form {:size "small"}
     [:> Form.Group
      [:> Form.Input {:label       "名称"
                      :inline      true
                      :on-change   #(reset! name (-> % .-target .-value))}]]
     [:div {:style {:text-align "center"}}
      [:> Button {:basic    true
                  :size     "small"
                  :icon "search"
                  :content  "查询:"
                  :on-click #(dispatch [:series/load-page @params])}]
      [:> Button {:color "green"
                  :icon "add"
                  :size     "small"
                  :content  "新增"
                  :on-click #(navigate! "/series/new")}]]]))

(defn list-table [{:keys [classes]}]
  (let [series-list (subscribe [:series/list])
        query-params (subscribe [:series/query-params])
        pagination (subscribe [:series/pagination])]
    [:div
     [:> Table {:celled     true
                :size       "small"
                :text-align "center"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell "序号"]
        [:> Table.HeaderCell "名称"]
        [:> Table.HeaderCell "简介"]
        [:> Table.HeaderCell "创建时间"]
        [:> Table.HeaderCell "更新时间"]
        [:> Table.HeaderCell "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id name description create_at update_at] :as series} @series-list]
           ^{:key series}
           [:> Table.Row
            [:> Table.Cell 1]
            [:> Table.Cell name]
            [:> Table.Cell description]
            [:> Table.Cell (du/to-date-time create_at)]
            [:> Table.Cell (du/to-date-time update_at)]
            [:> Table.Cell
             [:div
              [:> Button {:type     "primary"
                          :size     "small"
                          :alt      "修改"
                          :icon     "修改"
                          :on-click (fn []
                                      (do
                                        (dispatch [:series/load id])
                                        (set! *visible* true)))}]
              [:> Button {:type     "danger"
                          :size     "small"
                          :alt      "删除"
                          :icon     "delete"
                          :on-click (fn []
                                      (r/as-element
                                        (c/modal
                                          "删除"
                                          (str "你确认要删除吗？")
                                          #(dispatch [:series/delete id])
                                          #(js/console.log "cancel"))))}]]]]))]]
     (if @series-list
       [:div {:text-align "center"}
        [c/table-page :series/load-page (merge @query-params @pagination)]])]))

(defn home []
  [c/layout
   [:<>
    [query-form]
    [:> Divider]
    [list-table]]])