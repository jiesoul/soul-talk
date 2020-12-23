(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]))

(def ^:dynamic *edit-visible* (r/atom false))
(def ^:dynamic *add-visible* (r/atom false))
(def ^:dynamic *delete-dialog-open* (r/atom false))

(defn handle-close []
               (do (dispatch [:data-dices/clean-data-dic])
                   (reset! *add-visible* false)))
(defn handle-ok [data-dic]
  (dispatch [:data-dices/add data-dic]))

(defn add-form [{:keys [classes]}]
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])]
    (fn []
      [c/dialog {:open         @*add-visible*
                      :title "添加数据字典"
                      :on-close        #(handle-close)
                      :on-ok #(dispatch [:data-dices/add (assoc @data-dic :create_by user-id
                                                                          :update_by user-id)])}
       [:form {:name "add-data-dic-form"}
        [:> mui/TextField {:name       "id"
                           :label      "id"
                           :full-width true
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :id value]))}]
        [:> mui/TextField {:name       "name"
                           :label      "名称"
                           :full-width true
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :name value]))}]
        [:> mui/TextField {:name       "pid"
                           :label      "父id"
                           :full-width true
                           :rules      [{:required true}]
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :pid value]))}]
        [:> mui/TextField {:name       "note"
                           :label      "备注"
                           :full-width true
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:data-dices/set-attr :note value]))}]]])))

(defn edit-form [{:keys [classes]}]
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])]
    (fn []
      (if @data-dic
        (let [{:keys [id name pid note]} @data-dic]
          [c/dialog {:open            @*edit-visible*
                     :title "添加数据字典"
                     :aria-labelledby "add-form-dialog"
                     :on-close        #(reset! *edit-visible* false)
                     :on-ok #(dispatch [:data-dices/update (assoc @data-dic :update_by user-id)])}
           [:form {:name       "add-data-dic-form"
                   :class-name (.-root classes)}
            [:> mui/TextField {:id            "id"
                               :name          "id"
                               :label         "id"
                               :required      true
                               :full-width    true
                               :variant       "outlined"
                               :default-value id
                               :on-change     #(let [value (-> % .-target .-value)]
                                                 (dispatch [:data-dices/set-attr :id value]))}]
            [:> mui/TextField {:name       "name"
                               :label      "名称"
                               :full-width true
                               :variant    "outlined"
                               :value      name
                               :rules      [{:required true}]
                               :on-change  #(let [value (-> % .-target .-value)]
                                              (dispatch [:data-dices/set-attr :name value]))}]
            [:> mui/TextField {:name       "pid"
                               :label      "父id"
                               :full-width true
                               :variant    "outlined"
                               :value      pid
                               :rules      [{:required true}]
                               :on-change  #(let [value (-> % .-target .-value)]
                                              (dispatch [:data-dices/set-attr :pid value]))}]
            [:> mui/TextField {:name       "note"
                               :label      "备注"
                               :variant    "outlined"
                               :value      note
                               :full-width true
                               :on-change  #(let [value (-> % .-target .-value)]
                                              (dispatch [:data-dices/set-attr :note value]))}]]])))))

(def ^:dynamic *delete-id* (r/atom 0))

(defn delete-dialog []
  [c/dialog {:open     @*delete-dialog-open*
             :title    "删除数据字典"
             :ok-text  "确认"
             :on-close #(reset! *delete-dialog-open* false)
             :on-ok    #(do (reset! *delete-dialog-open* false)
                            (dispatch [:data-dices/delete @*delete-id*]))}
   [:> mui/DialogContentText "你确定要删除吗？"]])

(defn query-form [{:keys [classes] :as props}]
  (let [pagination (subscribe [:pagination])
        params (subscribe [:data-dices/query-params])]
    [:> mui/Paper {:class-name (.-paper classes)}
     [:form {:name       "query-form"
             :class-name (.-root classes)}
      [:div
       [:> mui/TextField {:name      "id"
                          :label     "id"
                          :on-change #(dispatch [:data-dices/set-query-params :id (-> % .-target .-value)])}]
       [:> mui/TextField {:name      "name"
                          :label     "名称"
                          :on-change #(dispatch [:data-dices/set-query-params :name (-> % .-target .-value)])}]
       [:> mui/TextField {:name      "pid"
                          :label     "父id"
                          :on-change #(dispatch [:data-dices/set-query-params :pid (-> % .-target .-value)])}]
       ]

      [:div {:class-name (.-buttons classes)}
       [:> mui/Button {:variant "outlined"
                       :color "default"
                       :size "small"
                       :type "reset"} "重置"]
       [:> mui/Button {:variant  "outlined"
                       :color    "primary"
                       :size     "small"
                       :on-click #(dispatch [:data-dices/load-page (merge @params @pagination)])}
        "查询"]
       [:> mui/Button {:variant  "outlined"
                       :color    "secondary"
                       :size     "small"
                       :on-click #(reset! *add-visible* true)}
        "新增"]]]]))

(defn list-table [{:keys [classes]}]
  (let [data-dices (subscribe [:data-dices])
        pagination (subscribe [:pagination])
        query-params (subscribe [:data-dices/query-params])]
    (fn []
      (let [{:keys [per_page page total total_pages]} @pagination]
        [:> mui/Paper {:class-name (.-paper classes)}
         [:> mui/TableContainer
          [:> mui/Table {:sticky-header true
                         :class-name    (.-table classes)
                         :size          "small"
                         :aria-label    "data-dices-table"}
           [:> mui/TableHead
            [:> mui/TableRow {:style {:background-color "blue"}}
             [:> mui/TableCell {:align "center" :class-name (.-head classes)} "ID"]
             [:> mui/TableCell {:align "center"} "名称"]
             [:> mui/TableCell {:align "center"} "PID"]
             [:> mui/TableCell {:align "center"} "备注"]
             [:> mui/TableCell {:align "center"} "创建时间"]
             [:> mui/TableCell {:align "center"} "创建人ID"]
             [:> mui/TableCell {:align "center"} "更新时间"]
             [:> mui/TableCell {:align "center"} "操作"]]]
           [:> mui/TableBody
            (doall
              (for [{:keys [id pid name note create_at create_by update_at] :as data-dic} @data-dices]
                ^{:key data-dic}
                [:> mui/TableRow {:tab-index  -1
                                  :class-name (.-row classes)}
                 [:> mui/TableCell {:align "center"} id]
                 [:> mui/TableCell {:align "center"} name]
                 [:> mui/TableCell {:align "center"} pid]
                 [:> mui/TableCell {:align "center"} note]
                 [:> mui/TableCell {:align "center"} (utils/to-date-time create_at)]
                 [:> mui/TableCell {:align "center"} create_by]
                 [:> mui/TableCell {:align "center"} (utils/to-date-time update_at)]
                 [:> mui/TableCell {:align "center"}
                  [:div
                   [:> mui/IconButton {:aria-label "edit"
                                       :color      "primary"
                                       :on-click   (fn []
                                                     (dispatch [:data-dices/load-data-dic id])
                                                     (reset! *edit-visible* true))}
                    [:> mui-icons/Edit]]
                   [:> mui/IconButton {:aria-label "delete"
                                       :color      "secondary"
                                       :style      {:margin "0 8px"}
                                       :on-click   (fn []
                                                     (do (reset! *delete-dialog-open* true)
                                                         (reset! *delete-id* id)))}
                    [:> mui-icons/Delete]]]]]))]]
          (if @data-dices
            [c/table-page :data-dices/load-page (merge @query-params @pagination)])]]))))

(defn query-page [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form add-form)
    (styles/styled-edit-form edit-form)
    (styles/styled-edit-form delete-dialog)
    (styles/styled-form query-form)
    (styles/styled-table list-table)]])

(defn home []
  (styles/main query-page))
