(ns soul-talk.article.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [soul-talk.routes :refer (navigate!)]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du :refer [to-date]]
            [soul-talk.common.md-editor :refer [editor]]
            [soul-talk.common.styles :as styles]
            ["@material-ui/core" :as mui :refer [Button Divider Table Form Row Col Input Layout Header
                                                 TextareaAutosize ]]
            ["@material-ui/icons" :as mui-icons]
            [soul-talk.utils :as utils]))

(defn- add-form [{:keys [classes]}]
  (let [article    (subscribe [:articles/edit])
        user    (subscribe [:user])
        user-id (:id @user)
        _ (dispatch [:articles/set-attr {:update_by user-id :create_by user-id :publish 0}])]
    [:> mui/Paper {:class-name (.-paper classes)}
     [:form {:name       "add-article-form"
             :class-name (.-root classes)}
      [:> Input {:name        "name"
                 :placeholder "请输入标题"
                 :required    true
                 :full-width  true
                 :on-change   #(dispatch [:articles/set-attr {:title (utils/event-value %)}])}]
      [:> TextareaAutosize {:rows        18
                            :cols        10
                            :placeholder "内容"
                            :class-name  (.-textArea classes)
                            :on-change   #(dispatch [:articles/set-attr {:body (utils/event-value %)}])}]

      [:div {:style      {:margin "normal"}
             :class-name (.-buttons classes)}
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "primary"

                       :on-click #(dispatch [:articles/add @article])}
        "保存"]
       [:> mui/Button {:type     "button"
                       :variant  "outlined"
                       :size     "small"
                       :color    "secondary"
                       :on-click #(js/history.go -1)}
        "返回"]]]]))

(defn- add-page [props]
  [c/layout props
   (styles/styled-edit-form add-form)])

(defn add []
  (styles/styled-layout add-page))

(defn- edit-form [{:keys [classes] :as props}]
  (let [article (subscribe [:articles/edit])
        user (subscribe [:user])
        user-id  (:id @user)
        _ (dispatch [:articles/set-attr {:update_by user-id}])]
    (let [{:keys [title body]} @article]
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "add-article-form"
               :class-name (.-root classes)}
        [:> mui/TextField {:name       "name"
                           :size       "small"
                           :required   true
                           :full-width true
                           :default-value     title
                           :on-change  #(let [value (-> % .-target .-value)]
                                          (dispatch [:articles/set-attr {:title value}]))}]
        [:> TextareaAutosize {:rows        18
                              :cols        10
                              :placeholder "内容"
                              :default-value body
                              :class-name  (.-textArea classes)
                              :on-change   #(dispatch [:articles/set-attr {:body (utils/event-value %)}])}]
        [:div {:style      {:margin "normal"}
               :class-name (.-buttons classes)}
         [:> mui/Button {:type     "button"
                         :variant  "outlined"
                         :size     "small"
                         :color    "primary"
                         :on-click #(dispatch [:articles/update @article])}
          "保存"]
         [:> mui/Button {:type     "button"
                         :variant  "outlined"
                         :size     "small"
                         :color    "secondary"
                         :on-click #(js/history.go -1)}
          "返回"]]]])))

(defn- edit-page [props]
  [c/layout props
   (styles/styled-edit-form edit-form)])

(defn edit []
  (styles/styled-layout edit-page))

(defn delete-dialog []
  (let [open (subscribe [:articles/delete-dialog-open])
        article (subscribe [:articles/edit])]
    (if @open
      [c/dialog {:open     @open
                 :title    (str "删除文章: ")
                 :ok-text  "确认"
                 :on-close #(dispatch [:articles/set-delete-dialog-open false])
                 :on-ok    #(do (dispatch [:articles/set-delete-dialog-open false])
                                (dispatch [:articles/delete (:id @article)]))}
       [:> mui/DialogContentText (str "你确定要删除\n" (:title @article) " 吗?")]])))

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:articles/query-params])]
    (fn []
      [:> mui/Paper {:class-name (.-paper classes)}
       [:form {:name       "query-form"
               :class-name (.-root classes)
               :size "small"}
        [:div
         [:> mui/TextField {:name      "name"
                            :label     "名称"
                            :size "small"
                            :on-change #(dispatch [:articles/set-query-params :name (-> % .-target .-value)])}]]
        [:div {:class-name (.-buttons classes)}
         [:> mui/Button {:color    "primary"
                         :size     "small"
                         :variant  "outlined"
                         :type "reset"
                         :on-click #(dispatch [:articles/clean-query-params])}
          "重置"]
         [:> mui/Button {:variant  "outlined"
                         :size     "small"
                         :color    "primary"
                         :on-click #(dispatch [:articles/load-page @query-params])}
          "搜索"]
         [:> mui/Button {:color    "secondary"
                         :size     "small"
                         :variant  "outlined"
                         :on-click #(navigate! "/articles/add")}
          "新增"]]]])))

(defn list-table [{:keys [classes]}]
  (let [articles (subscribe [:articles])
        pagination (subscribe [:articles/pagination])
        query-params (subscribe [:articles/query-params])]
    (fn []
      [:> mui/TableContainer {:class-name (.-paper classes)
                              :component  mui/Paper}
       [:> mui/Table {:class-name    (.-table classes)
                      :sticky-header true
                      :aria-label    "list-table"
                      :size          "small"}
        [:> mui/TableHead {:class-name (.-head classes)}
         [:> mui/TableRow {:class-name (.-head classes)}
          [:> mui/TableCell {:align "center"} "序号"]
          [:> mui/TableCell {:align "center"} "ID"]
          [:> mui/TableCell {:align "center"} "标题"]
          [:> mui/TableCell {:align "center"} "简介"]
          [:> mui/TableCell {:align "center"} "作者"]
          [:> mui/TableCell {:align "center"} "发布状态"]
          [:> mui/TableCell {:align "center"} "浏览量"]
          [:> mui/TableCell {:align "center"} "创建时间"]
          [:> mui/TableCell {:align "center"} "更新时间"]
          [:> mui/TableCell {:align "center"} "操作"]
          ]]
        [:> mui/TableBody {:class-name (.-body classes)}
         (let [{:keys [offset per_page]} @pagination]
           (doall
             (for [{:keys [index id title description publish pv create_by create_at update_by update_at] :as article}
                   (map #(assoc %1 :index %2) @articles (range offset (+ offset per_page)))]
               ^{:key article}
               [:> mui/TableRow {:class-name (.-row classes)}
                [:> mui/TableCell {:align "center"} (inc index)]
                [:> mui/TableCell {:align "center"} id]
                [:> mui/TableCell {:align "center"} title]
                [:> mui/TableCell {:align "center"} description]
                [:> mui/TableCell {:align "center"} publish]
                [:> mui/TableCell {:align "center"} pv]
                [:> mui/TableCell {:align "center"} create_by]
                [:> mui/TableCell {:align "center"} (du/to-date-time create_at)]
                [:> mui/TableCell {:align "center"} (du/to-date-time update_at)]
                [:> mui/TableCell {:align "center"}
                 [:div
                  [:> mui/IconButton {:color    "primary"
                                      :size     "small"
                                      :on-click #(navigate! (str "/articles/" id "/edit"))}
                   [:> mui-icons/Edit]]

                  [:> mui/IconButton {:color    "secondary"
                                      :size     "small"
                                      :style    {:margin "0 8px"}
                                      :on-click (fn []
                                                  (do
                                                    (dispatch [:articles/set-delete-dialog-open true])
                                                    (dispatch [:articles/set-attr {:id id :title title}])))}
                   [:> mui-icons/Delete]]]]])))]]
       (if @articles
         [c/table-page :articles/load-page (merge @query-params @pagination)])])))

(defn query-page
  [props]
  [c/layout props
   [:<>
    (styles/styled-edit-form delete-dialog)
    (styles/styled-form query-form)
    (styles/styled-table list-table)
    ]])

(defn home []
  (styles/styled-layout query-page))



