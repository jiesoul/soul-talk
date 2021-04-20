(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]
            [reagent.core :as r]
            [soul-talk.routes :refer [navigate!]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]))

(defn new-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic/edit])
        _        (dispatch [:data-dic/set-attr {:create_by user-id
                                                  :update_by user-id}])]
    [c/form-layout
     [:> Form {:name "add-data-dic-form"}
      [:> Form.Input {:name      "id"
                      :label     "id"
                      :required  true
                      :on-change #(let [value (-> % .-target .-value)]
                                    (dispatch [:data-dic/set-attr {:id value}]))}]
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :required  true
                      :on-change #(let [value (-> % .-target .-value)]
                                    (dispatch [:data-dic/set-attr {:name value}]))}]
      [:> Form.Input {:name      "pid"
                      :label     "父id"
                      :required  true
                      :on-change #(let [value (-> % .-target .-value)]
                                    (dispatch [:data-dic/set-attr {:pid value}]))}]
      [:> Form.Input {:name      "note"
                      :label     "备注"
                      :on-change #(let [value (-> % .-target .-value)]
                                    (dispatch [:data-dic/set-attr {:note value}]))}]

      [:div {:style {:text-align "center"}}
       [:> Button {:content  "返回"
                   :on-click #(navigate! (str "/data-dic"))}]
       [:> Button {:content  "保存"
                   :positive true
                   :on-click #(dispatch [:data-dic/save @data-dic])}]]]]))


(defn new []
  [c/layout [new-form]])

(defn edit-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic/edit])
        _        (dispatch [:data-dic/set-attr {:update_by user-id}])]
    [c/form-layout
     [:> Form {:name "add-data-dic-form"}
      [:> Form.Input {:name          "id"
                      :label         "id"
                      :required      true
                      :default-value (:id @data-dic)
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:data-dic/set-attr {:id value}]))}]
      [:> Form.Input {:name          "name"
                      :label         "名称"
                      :required      true
                      :default-value (:name @data-dic)
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:data-dic/set-attr {:name value}]))}]
      [:> Form.Input {:name          "pid"
                      :label         "父id"
                      :required      true
                      :default-value (:pid @data-dic)
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:data-dic/set-attr {:pid value}]))}]
      [:> Form.Input {:name          "note"
                      :label         "备注"
                      :default-value (:note @data-dic)
                      :on-change     #(let [value (-> % .-target .-value)]
                                        (dispatch [:data-dic/set-attr {:note value}]))}]

      [:div {:style {:text-align "center"}}
       [:> Button {:content  "返回"
                   :on-click #(navigate! (str "/data-dic"))}]
       [:> Button {:content  "保存"
                   :positive true
                   :on-click #(dispatch [:data-dic/update @data-dic])}]]]]))

(defn edit []
  [c/layout [edit-form]])

(defn delete-dialog []
  (let [open (subscribe [:data-dic/delete-dialog])
        data-dic (subscribe [:data-dic/edit])]
    [c/confirm {:open     @open
                :title    "删除数据字典"
                :ok-text  "确认"
                :on-close #(dispatch [:data-dic/set-delete-dialog false])
                :on-ok    #(do (dispatch [:data-dic/set-delete-dialog false])
                               (dispatch [:data-dic/delete (:id @data-dic)]))}
     "你确定要删除吗？"]))

(defn query-form []
  (let [pagination (subscribe [:data-dic/pagination])
        params (subscribe [:data-dic/query-params])]
    [:> Form {:name "query-form"}
     [:> Form.Group {:inline true}
      [:> Form.Input {:name      "id"
                      :label     "id"
                      :on-change #(dispatch [:data-dic/set-query-params :id (-> % .-target .-value)])}]
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :on-change #(dispatch [:data-dic/set-query-params :name (-> % .-target .-value)])}]
      [:> Form.Input {:name      "pid"
                      :label     "父id"
                      :on-change #(dispatch [:data-dic/set-query-params :pid (-> % .-target .-value)])}]]

     [:div {:style {:text-align "center"}}
      [:> Button {:on-click #(dispatch [:data-dic/load-page (merge @params @pagination)])}
       "查询"]
      [:> Button {:positive true
                  :on-click #(navigate! "/data-dic/new")}
       "新增"]]]))

(defn list-table []
  (let [data-dices (subscribe [:data-dic/list])
        pagination (subscribe [:data-dic/pagination])
        query-params (subscribe [:data-dic/query-params])]
    [:<>
     [:> Table {:celled     true
                :text-align "center"
                :aria-label "data-dic-table"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell "ID"]
        [:> Table.HeaderCell "名称"]
        [:> Table.HeaderCell "PID"]
        [:> Table.HeaderCell "备注"]
        [:> Table.HeaderCell "创建时间"]
        [:> Table.HeaderCell "创建人ID"]
        [:> Table.HeaderCell "更新时间"]
        [:> Table.HeaderCell "操作"]]]
      [:> Table.Body
       (doall
         (for [{:keys [id pid name note create_at create_by update_at] :as data-dic} @data-dices]
           ^{:key data-dic}
           [:> Table.Row {:tab-index -1}
            [:> Table.Cell id]
            [:> Table.Cell name]
            [:> Table.Cell pid]
            [:> Table.Cell note]
            [:> Table.Cell (utils/to-date-time create_at)]
            [:> Table.Cell create_by]
            [:> Table.Cell (utils/to-date-time update_at)]
            [:> Table.Cell
             [:div
              [:> Button {:positive true
                          :icon     "edit"
                          :on-click #(navigate! (str "/data-dic/" id "/edit"))}]
              [:> Button {:negative true
                          :icon     "delete"
                          :on-click (fn []
                                      (do (dispatch [:data-dic/set-delete-dialog true])
                                          (dispatch [:data-dic/set-attr data-dic])))}]]]]))]]
     (if @data-dices
       [c/table-page :data-dic/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [:> Divider]
    [list-table]]])
