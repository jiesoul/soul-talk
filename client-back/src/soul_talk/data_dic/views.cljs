(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input]]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]))

(def ^:dynamic *delete-dialog-open* (r/atom false))

(defn handle-ok [data-dic]
  (dispatch [:data-dices/add data-dic]))

(defn add-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])
        data-dic (assoc @data-dic :create_by user-id
                                  :update_by user-id)]
    [:> Form {:name "add-data-dic-form"}
     [:> Form.Input {:name       "id"
                        :label      "id"
                        :full-width true
                        :rules      [{:required true}]
                        :on-change  #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :id value]))}]
     [:> Form.Input {:name       "name"
                        :label      "名称"
                        :full-width true
                        :rules      [{:required true}]
                        :on-change  #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :name value]))}]
     [:> Form.Input {:name       "pid"
                        :label      "父id"
                        :full-width true
                        :rules      [{:required true}]
                        :on-change  #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :pid value]))}]
     [:> Form.Input {:name       "note"
                        :label      "备注"
                        :full-width true
                        :on-change  #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :note value]))}]]))


(defn add []
  [c/layout [add-form]])

(defn edit-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])
        data-dic (assoc @data-dic :update_by user-id)]
    (fn []
      (if @data-dic
        (let [{:keys [id name pid note]} @data-dic]
          [:> Form {:name "add-data-dic-form"}
           [:> Form.Input {:id            "id"
                           :name          "id"
                           :label         "id"
                           :required      true
                           :full-width    true
                           :variant       "outlined"
                           :default-value id
                           :on-change     #(let [value (-> % .-target .-value)]
                                             (dispatch [:data-dices/set-attr :id value]))}]
           [:> Form.Input {:name       "name"
                           :label      "名称"
                           :full-width true
                           :variant    "outlined"
                           :value      name
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :name value]))}]
           [:> Form.Input {:name       "pid"
                           :label      "父id"
                           :full-width true
                           :variant    "outlined"
                           :value      pid
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :pid value]))}]
           [:> Form.Input {:name       "note"
                           :label      "备注"
                           :variant    "outlined"
                           :value      note
                           :full-width true
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :note value]))}]])))))

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
              :size "mini"}
     [:> Form.Group {:inline true}
      [:> Form.Input {:name      "id"
                      :label     "id"
                      :on-change #(dispatch [:data-dices/set-query-params :id (-> % .-target .-value)])}]
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :on-change #(dispatch [:data-dices/set-query-params :name (-> % .-target .-value)])}]
      [:> Form.Input {:name      "pid"
                      :label     "父id"
                      :on-change #(dispatch [:data-dices/set-query-params :pid (-> % .-target .-value)])}]
      ]

     [:div {:style {:text-align "center"}}
      [:> Button {:basic true
                  :color "green"
                  :size     "mini"
                  :on-click #(dispatch [:data-dices/load-page (merge @params @pagination)])}
       "查询"]
      [:> Button {:basic true
                  :color    "red"
                  :size     "mini"
                  :on-click #()}
       "新增"]]]))

(defn list-table []
  (let [data-dices (subscribe [:data-dices])
        pagination (subscribe [:pagination])
        query-params (subscribe [:data-dices/query-params])]
    (let [{:keys [per_page page total total_pages]} @pagination]
      [:<>
       [:> Table {:celled true
                  :size          "mini"
                  :aria-label    "data-dices-table"}
        [:> Table.Header
         [:> Table.Row
          [:> Table.HeaderCell {:align "center"} "ID"]
          [:> Table.HeaderCell {:align "center"} "名称"]
          [:> Table.HeaderCell {:align "center"} "PID"]
          [:> Table.HeaderCell {:align "center"} "备注"]
          [:> Table.HeaderCell {:align "center"} "创建时间"]
          [:> Table.HeaderCell {:align "center"} "创建人ID"]
          [:> Table.HeaderCell {:align "center"} "更新时间"]
          [:> Table.HeaderCell {:align "center"} "操作"]]]
        [:> Table.Body
         (doall
           (for [{:keys [id pid name note create_at create_by update_at] :as data-dic} @data-dices]
             ^{:key data-dic}
             [:> Table.Row {:tab-index -1}
              [:> Table.Cell {:align "center"} id]
              [:> Table.Cell {:align "center"} name]
              [:> Table.Cell {:align "center"} pid]
              [:> Table.Cell {:align "center"} note]
              [:> Table.Cell {:align "center"} (utils/to-date-time create_at)]
              [:> Table.Cell {:align "center"} create_by]
              [:> Table.Cell {:align "center"} (utils/to-date-time update_at)]
              [:> Table.Cell {:align "center"}
               [:div
                [:> Button {:basic true
                            :color      "green"
                            :icon       "edit"
                            :size "mini"
                            :on-click   (fn []
                                          (dispatch [:data-dices/load-data-dic id]))}]
                [:> Button {:basic true
                            :size "mini"
                            :color      "red"
                            :style      {:margin "0 8px"}
                            :icon       "delete"
                            :on-click   (fn []
                                          (do (reset! *delete-dialog-open* true)
                                              (reset! *delete-id* id)))}]]]]))]]
       (if @data-dices
         [c/table-page :data-dices/load-page (merge @query-params @pagination)])])))

(defn home []
  [c/layout
   [:<>
    [delete-dialog]
    [query-form]
    [list-table]]])
