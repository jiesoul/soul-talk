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
     [c/form-layout
      [:> Form
       [:> Form.Input {:title     "name"
                       :label     "名称"
                       :required  true
                       :on-change #(dispatch [:series/set-attr {:name (du/event-value %)}])}]
       [:> Form.Input {:label     "简介"
                       :required  true
                       :on-change #(dispatch [:series/set-attr {:description (du/event-value %)}])}]
       [:div.button-center
        [:> Button {:content  "返回"
                    :on-click #(navigate! (str "/series"))}]
        [:> Button {:content  "保存"
                    :positive true
                    :on-click #(dispatch [:series/save @series])}]]
       ]]]))

(defn edit []
  (let [user (subscribe [:user])
        series (subscribe [:series/edit])
        _ (dispatch [:series/set-attr {:update_by (:id @user)}])]
    [c/layout
     [c/form-layout
      [:> Form
       [:> Form.Input {:title         "name"
                       :label         "name"
                       :required      true
                       :default-value (:name @series)
                       :on-change     #(dispatch [:series/set-attr {:name (du/event-value %)}])}]
       [:> Form.Input {:label         "简介"
                       :required      true
                       :default-value (:description @series)
                       :on-change     #(dispatch [:series/set-attr {:description (du/event-value %)}])}]
       [:div.button-center
        [:> Button {:content  "返回"
                    :on-click #(navigate! (str "/series"))}]
        [:> Button {:content  "保存"
                    :positive true
                    :on-click #(dispatch [:series/update @series])}]]
       ]]]))

(defn- delete-modal []
  (let [series (subscribe [:series/edit])
        delete-status (subscribe [:series/delete-dialog])]
    (if @series
      (let [{:keys [id name]} @series]
        ^{:key "delete-series-dialog"}
        [c/confirm {:open   @delete-status
                  :title    "删除菜单"
                  :ok-text  "确认"
                  :on-close #(dispatch [:series/set-delete-dialog false])
                  :on-ok    #(do
                               (dispatch [:series/set-delete-dialog false])
                               (dispatch [:series/delete id]))}
         (str "你确定要删除系列 " name " 吗？")]))))

(defn query-form []
  (let [params (subscribe [:series/query-params])]
    [:> Form
     [:> Form.Group
      [:> Form.Input {:label       "名称"
                      :inline      true
                      :on-change   #(reset! name (-> % .-target .-value))}]]
     [:div.button-center
      [:> Button {:basic    true
                  :icon "search"
                  :content  "查询"
                  :on-click #(dispatch [:series/load-page @params])}]
      [:> Button {:color "green"
                  :icon "add"
                  :content  "新增"
                  :on-click #(navigate! "/series/new")}]]]))

(defn list-table []
  (let [series-list (subscribe [:series/list])
        query-params (subscribe [:series/query-params])
        pagination (subscribe [:series/pagination])]
    [:div
     [:> Table {:celled     true
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
              [:> Button {:color "green"
                          :alt      "修改"
                          :icon     "edit"
                          :on-click #(navigate! (str "/series/" id "/edit"))}]
              [:> Button {:color    "red"
                          :alt      "删除"
                          :icon     "delete"
                          :on-click #(do
                                      (dispatch [:series/set-attr {:id id :name name}])
                                      (dispatch [:series/set-delete-dialog true]))}]]]]))]]
     (if @series-list
       [c/table-page :series/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [query-form]
    [:> Divider]
    [delete-modal]
    [list-table]]])