(ns soul-talk.series.views
  (:require [soul-talk.common.views :as c]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [soul-talk.utils :as du]
            [soul-talk.common.styles :as styles]
            ["@material-ui/core" :as mui :refer [Paper Button Divider TextField TableContainer]]
            ["@material-ui/icons" :refer [Search Edit Delete]]
            ["@material-ui/data-grid" :refer [DataGrid]]))

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
    (fn []
      [c/dialog {:visible    @*visible*
                 :title      "添加系列"
                 :okText     "保存"
                 :cancelText "退出"
                 :onCancel   #(do
                                (dispatch [:tags/clean-tag])
                                (reset! *visible* false))
                 :onOk       #(if @id
                                (dispatch [:series/add @update-series])
                                (dispatch [:series/update @update-series]))}
       [:form {:name "add_tag_form"}
        [:> TextField {:title "name"
                       :label "name"
                       :required true
                       :rules [{:require true :message "please enter name"}]
                       :on-change #(let [value (-> % .-target .-value)]
                                     (reset! name value))}]]])))

(defn query-form [{:keys [classes]}]
  (let [params (r/atom {})
        name (r/cursor params [:name])]
    [:> Paper {:class-name (.-paper classes)}
     [:form {:title     ""
             :class-name (.-root classes)}
      [:div
       [:> TextField {:label       "名称"
                      :placeholder "name"
                      :on-blur     #(reset! name (-> % .-target .-value))}]]
      [:div {:class-name (.-buttons classes)}
       [:div
        [:> Button {:variant  "outlined"
                    :color    "primary"
                    :size     "small"
                    :on-click #(dispatch [:series/load-page @params])}
         "查询"]
        [:> Button {:variant  "outlined"
                    :size     "small"
                    :style    {:margin "0 8px"}
                    :on-click #(dispatch [:series/set-add-dialog true])}
         "新增"]]]]]))

(defn list-table [{:keys [classes]}]
  (let [series-list (subscribe [:series/list])
        query-params (subscribe [:series/query-params])
        pagination (subscribe [:series/pagination])]
    [:> mui/TableContainer {:class-name (.-paper classes)
                            :component  mui/Paper}
     [:> mui/Table {:sticky-header true
                    :aria-label    "list-table"
                    :size          "small"
                    :class-name (.-table classes)}
      [:> mui/TableHead {:class-name (.-head classes)}
       [:> mui/TableRow {:class-name (.-head classes)}
        [:> mui/TableCell {:align "center"} "序号"]
        [:> mui/TableCell {:align "center"} "名称"]
        [:> mui/TableCell {:align "center"} "简介"]
        [:> mui/TableCell {:align "center"} "创建时间"]
        [:> mui/TableCell {:align "center"} "更新时间"]
        [:> mui/TableCell {:align "center"} "操作"]]]]
     [:> mui/TableBody {:class-name (.-body classes)}
      (doall
        (for [{:keys [id name description create_at update_at] :as series} @series-list]
          ^{:key series}
          [:> mui/TableRow
           [:> mui/TableCell {:align "center"} 1]
           [:> mui/TableCell {:align "center"} name]
           [:> mui/TableCell {:align "center"} description]
           [:> mui/TableCell {:align "center"} (du/to-date-time create_at)]
           [:> mui/TableCell {:align "center"} (du/to-date-time update_at)]
           [:> mui/TableCell {:align "center"}
            [:div
             [:> mui/IconButton {:type     "primary"
                                 :size     "small"
                                 :alt      "修改"
                                 :on-click (fn []
                                             (do
                                               (dispatch [:series/load id])
                                               (set! *visible* true)))}
              [:> Edit]]
             [:> mui/Divider {:type "vertical"}]
             [:> mui/IconButton {:type     "danger"
                                 :size     "small"
                                 :alt      "删除"
                                 :on-click (fn []
                                             (r/as-element
                                               (c/dialog
                                                 "删除"
                                                 (str "你确认要删除吗？")
                                                 #(dispatch [:series/delete id])
                                                 #(js/console.log "cancel"))))}
              [:> Delete]]]]]))]]))

(defn query-page
  [props]
  [c/layout props
   [:div
    (styles/styled-form query-form)
    (styles/styled-table list-table)]])

(defn home []
  (styles/styled-layout query-page))