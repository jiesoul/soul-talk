(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]
            [reagent.core :as r]
            [soul-talk.routes :refer [navigate!]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]))

(def ^:dynamic *delete-dialog-open* (r/atom false))

(defn new-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])
        _        (dispatch [:data-dices/set-attr {:create_by user-id
                                                  :update_by user-id}])]
    [:> Form {:name "add-data-dic-form"}
     [:> Form.Input {:name      "id"
                     :label     "id"
                     :required  true
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:id value}]))}]
     [:> Form.Input {:name      "name"
                     :label     "名称"
                     :required  true
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:name value}]))}]
     [:> Form.Input {:name      "pid"
                     :label     "父id"
                     :required  true
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:pid value}]))}]
     [:> Form.Input {:name      "note"
                     :label     "备注"
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:note value}]))}]

     [:div {:style {:text-align "center"}}
      [:> Button.Group {:size "mini"
                        :compact true}
       [:> Button {:content  "返回"
                   :on-click #(navigate! (str "/data-dices"))}]
       [:> Button.Or]
       [:> Button {:content  "保存"
                   :positive true
                   :on-click #(dispatch [:data-dices/new @data-dic])}]]]]))


(defn new []
  [c/layout [new-form]])

(defn edit-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])
        _        (dispatch [:data-dices/set-attr {:update_by user-id}])]
    [:> Form {:name "add-data-dic-form"}
     [:> Form.Input {:name      "id"
                     :label     "id"
                     :required  true
                     :default-value (:id @data-dic)
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:id value}]))}]
     [:> Form.Input {:name      "name"
                     :label     "名称"
                     :required  true
                     :default-value (:name @data-dic)
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:name value}]))}]
     [:> Form.Input {:name      "pid"
                     :label     "父id"
                     :required  true
                     :default-value (:pid @data-dic)
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:pid value}]))}]
     [:> Form.Input {:name      "note"
                     :label     "备注"
                     :default-value (:note @data-dic)
                     :on-change #(let [value (-> % .-target .-value)]
                                   (dispatch [:data-dices/set-attr {:note value}]))}]

     [:div {:style {:text-align "center"}}
      [:> Button.Group {:size "mini"
                        :compact true}
       [:> Button {:content  "返回"
                   :on-click #(navigate! (str "/data-dices"))}]
       [:> Button.Or]
       [:> Button {:content  "保存"
                   :positive true
                   :on-click #(dispatch [:data-dices/edit @data-dic])}]]]]))

(defn edit []
  [c/layout [edit-form]])

(def ^:dynamic *delete-id* (r/atom 0))

(defn delete-dialog []
  [c/modal {:open      @*delete-dialog-open*
             :title    "删除数据字典"
             :ok-text  "确认"
             :on-close #(reset! *delete-dialog-open* false)
             :on-ok    #(do (reset! *delete-dialog-open* false)
                            (dispatch [:data-dices/delete @*delete-id*]))}
   "你确定要删除吗？"])

(defn query-form []
  (let [pagination (subscribe [:pagination])
        params (subscribe [:data-dices/query-params])]
    [:> Form {:name "query-form"
              :size "small"}
     [:> Form.Group {:inline true}
      [:> Form.Input {:name      "id"
                      :label     "id"
                      :on-change #(dispatch [:data-dices/set-query-params :id (-> % .-target .-value)])}]
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :on-change #(dispatch [:data-dices/set-query-params :name (-> % .-target .-value)])}]
      [:> Form.Input {:name      "pid"
                      :label     "父id"
                      :on-change #(dispatch [:data-dices/set-query-params :pid (-> % .-target .-value)])}]]

     [:div {:style {:text-align "center"}}
      [:> Button.Group {:size "mini"
                        :compact true}
       [:> Button {:on-click #(dispatch [:data-dices/load-page (merge @params @pagination)])}
        "查询"]
       [:> Button.Or]
       [:> Button {:positive true
                   :on-click #(navigate! "/data-dices/new")}
        "新增"]]]]))

(defn list-table []
  (let [data-dices (subscribe [:data-dices])
        pagination (subscribe [:pagination])
        query-params (subscribe [:data-dices/query-params])]
    (let [{:keys [per_page page total total_pages]} @pagination]
      [:<>
       [:> Table {:celled true
                  :text-align "center"
                  :aria-label    "data-dices-table"}
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
                [:> Button.Group {:size "mini"
                                  :compact true}
                 [:> Button {:positive true
                             :content "编辑"
                             :on-click #(navigate! (str "/data-dices/" id "/edit"))}]
                 [:> Button.Or]
                 [:> Button {:negative true
                             :content "删除"
                             :on-click (fn []
                                         (do (reset! *delete-dialog-open* true)
                                             (reset! *delete-id* id)))}]]]]]))]]
       (if @data-dices
         [c/table-page :data-dices/load-page (merge @query-params @pagination)])])))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [:> Divider]
    [list-table]]])
