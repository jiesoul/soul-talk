(ns soul-talk.data-dic.views
  (:require [soul-talk.common.views :as c]
            [antd :refer [Row Col Form Input Button Divider Table Modal]]
            ["@ant-design/icons" :as antd-icons :refer [EditOutlined DeleteOutlined]]
            ["@material-ui/core" :as mui]
            ["@material-ui/icons" :as mui-icons]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]))

(def ^:dynamic *edit-visible* (r/atom false))
(def ^:dynamic *add-visible* (r/atom false))

(defn add-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])]
    (fn []
      [:> Modal {:visible    @*add-visible*
                 :title      "添加数据字典"
                 :okText     "保存"
                 :cancelText "退出"
                 :onCancel   #(do
                                (dispatch [:data-dices/clean-data-dic])
                                (reset! *add-visible* false))
                 :onOk       #(let [data-dic @data-dic]
                                (assoc data-dic :update_by user-id)
                                (dispatch [:data-dices/add (assoc data-dic :create_by user-id)]))}
       [:form {:name              "add-data-dic-form"
                 :validate-messages c/validate-messages
                 :initial-values @data-dic
                 :labelCol          {:span 8}
                 :wrapperCol        {:span 8}}
        [:> Form.Item {:name      "id"
                       :label     "id"
                       :rules     [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :id value]))}
         [:> Input]]
        [:> Form.Item {:name      "name"
                       :label     "名称"
                       :rules     [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :name value]))}
         [:> Input]]
        [:> Form.Item {:name      "pid"
                       :label     "父id"
                       :rules     [{:required true}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :pid value]))}
         [:> Input]]
        [:> Form.Item {:name      "note"
                       :label     "备注"
                       :on-change #(let [value (-> % .-target .-value)]
                                     (dispatch [:data-dices/set-attr :note value]))}
         [:> Input]]
        ]])))

(defn edit-form []
  (let [user-id  (:id @(subscribe [:user]))
        data-dic (subscribe [:data-dic])]
    (fn []
      (if @data-dic
        [:> Modal {:visible    @*edit-visible*
                   :title      "编辑数据字典"
                   :okText     "保存"
                   :cancelText "退出"
                   :onCancel   #(reset! *edit-visible* false)
                               :onOk #(let [data-dic @data-dic]
                                        (assoc data-dic :update_by user-id)
                                        (dispatch [:data-dices/update data-dic]))}
         [:> Form {:name              "add-data-dic-form"
                   :initial-values    @data-dic
                   :validate-messages c/validate-messages
                   :labelCol          {:span 8}
                   :wrapperCol        {:span 8}}
          [:> Form.Item {:name      "id"
                         :label     "id"
                         :rules     [{:required true}]}
           [:> Input {:disabled true}]]
          [:> Form.Item {:name      "name"
                         :label     "名称"
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :name value]))}
           [:> Input]]
          [:> Form.Item {:name      "pid"
                         :label     "父id"
                         :rules     [{:required true}]
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :pid value]))}
           [:> Input]]
          [:> Form.Item {:name      "note"
                         :label     "备注"
                         :on-change #(let [value (-> % .-target .-value)]
                                       (dispatch [:data-dices/set-attr :note value]))}
           [:> Input]]]]))))

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

      [:div {:style {:text-align "right"
                     :margin-top "5px"}}
       [:> mui/Button {:variant  "outlined"
                       :color    "primary"
                       :size     "small"
                       :on-click #(dispatch [:data-dices/load-page (merge @params @pagination)])}
        "查询"]
       [:> mui/Button {:variant  "outlined"
                       :color    "secondary"
                       :size     "small"
                       :style    {:margin "0 8px"}
                       :on-click #(reset! *add-visible* true)}
        "新增"]]]]))

(def list-columns
  [{:title "ID" :dataIndex "id", :key "id", :align "center"}
   {:title "名称" :dataIndex "name", :key "name", :align "center"}
   {:title "父ID" :dataIndex "pid", :key "pid", :align "center"}
   {:title "备注" :dataIndex "note", :key "note", :align "center"}
   {:title  "创建时间" :dataIndex "create_at" :key "create_at" :align "center"}
   {:title "创建人" :dataIndex "create_by", :key "create_by", :align "center"}
   {:title  "更新时间" :dataIndex "update_at" :key "update_at" :align "center"}
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"}])

(defn list-table [{:keys [classes]}]
  (let [data-dices (subscribe [:data-dices])
        pagination (subscribe [:pagination])]
    (fn []
      (let [{:keys [per_page page total total_pages]} @pagination]
        [:> mui/Paper {:class-name (.-paper classes)}
         [:> mui/TableContainer {:class-name (.-container classes)}
          [:> mui/Table {:sticky-header true
                         :aria-label    "data-dices-table"
                         :size "small"}
           [:> mui/TableHead {:class-name (.-head classes)}
            [:> mui/TableRow {:class-name (.-head classes)}
             (doall
               (for [column list-columns]
                 ^{:key column}
                 [:> mui/TableCell {:key   (:key column)
                                    :align (:align column)
                                    :style {:min-width (:min-width column)}}
                  (:title column)]))]]
           [:> mui/TableBody
            (doall
              (for [{:keys [id pid name note create_at create_by update_at] :as data-dic} @data-dices]
                ^{:key data-dic}
                [:> mui/TableRow {:key       (str "tr" id)
                                  :hover     true
                                  :tab-index -1
                                  :class-name (.-row classes)}
                 [:> mui/TableCell {:key id :align "center"} id]
                 [:> mui/TableCell {:key name :align "center"} name]
                 [:> mui/TableCell {:key pid :align "center"} pid]
                 [:> mui/TableCell {:key note :align "center"} note]
                 [:> mui/TableCell {:key create_at :align "center"} (utils/to-date-time create_at)]
                 [:> mui/TableCell {:key create_by :align "center"} create_by]
                 [:> mui/TableCell {:key (str "update" update_at) :align "center"} (utils/to-date-time update_at)]
                 [:> mui/TableCell {:key (str "action" id) :align "center"}
                  [:div
                   [:> mui/IconButton {:aria-label "edit"
                                       :color "primary"
                                       :on-click   (fn []
                                                     (dispatch [:data-dices/load-data-dic id])
                                                     (reset! *edit-visible* true))}
                    [:> mui-icons/Edit]]

                   [:> mui/IconButton {:aria-label "delete"
                                       :color "secondary"
                                       :style    {:margin "0 8px"}
                                       :on-click (fn []
                                                   (r/as-element
                                                     (c/show-confirm
                                                       "删除"
                                                       (str "你确认要删除吗？")
                                                       #(dispatch [:data-dices/delete id])
                                                       #(js/console.log "cancel"))))}
                    [:> mui-icons/Delete]]]]]))]]]
         [:> mui/TablePagination {:rows-per-page-options [10, 20, 100]
                                  :component             "div"
                                  :color "primary"
                                  :variant "outlined"
                                  :count total_pages
                                  :page page
                                  :rows-per-page per_page}]]))))

(defn query-page [props]
  [c/layout props
   [:div
    [add-form]
    (styles/with-custom-styles query-form styles/form-styles)
    [edit-form]
    (styles/with-custom-styles list-table styles/table-styles)
    ]])

(defn home []
  (styles/main query-page))
