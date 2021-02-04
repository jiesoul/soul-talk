(ns soul-talk.article.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [soul-talk.routes :refer (navigate!)]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du :refer [to-date]]
            [soul-talk.common.md-editor :refer [editor]]
            [soul-talk.common.styles :as styles]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input TextArea TextArea]]
            [soul-talk.utils :as utils]))

(defn- add-form []
  (let [article    (subscribe [:articles/edit])
        user    (subscribe [:user])
        user-id (:id @user)
        _ (dispatch [:articles/set-attr {:update_by user-id :create_by user-id :publish 0}])]
    [:> Form {:name       "add-article-form"}
     [:> Input {:name        "name"
                :placeholder "请输入标题"
                :required    true
                :full-width  true
                :on-change   #(dispatch [:articles/set-attr {:title (utils/event-value %)}])}]
     [:> TextArea {:rows        18
                   :cols        10
                   :placeholder "内容"
                   :on-change   #(dispatch [:articles/set-attr {:body (utils/event-value %)}])}]

     [:div {:style      {:text-align "center"}}
      [:> Button {:basic true
                  :size     "mini"
                  :color    "primary"
                  :on-click #(dispatch [:articles/add @article])}
       "保存"]
      [:> Button {:basic true
                  :size  "mini"
                  :color    "secondary"
                  :on-click #(js/history.go -1)}
       "返回"]]]))

(defn add []
  [c/layout [add-form]])

(defn- edit-form [{:keys [classes] :as props}]
  (let [article (subscribe [:articles/edit])
        user (subscribe [:user])
        user-id  (:id @user)
        _ (dispatch [:articles/set-attr {:update_by user-id}])]
    (let [{:keys [title body]} @article]
      [:> Form {:name       "add-article-form"}
       [:> Form.Input {:name          "name"
                       :size          "small"
                       :required      true
                       :full-width    true
                       :default-value title
                       :on-change     #(let [value (-> % .-target .-value)]
                                         (dispatch [:articles/set-attr {:title value}]))}]
       [:> TextArea {:rows          18
                     :cols          10
                     :placeholder   "内容"
                     :default-value body
                     :class-name    (.-textArea classes)
                     :on-change     #(dispatch [:articles/set-attr {:body (utils/event-value %)}])}]
       [:div {:style      {:text-align "center"}}
        [:> Button {:type     "button"
                    :variant  "outlined"
                    :size     "small"
                    :color    "primary"
                    :on-click #(dispatch [:articles/update @article])}
         "保存"]
        [:> Button {:type     "button"
                    :variant  "outlined"
                    :size     "small"
                    :color    "secondary"
                    :on-click #(js/history.go -1)}
         "返回"]]])))

(defn edit []
  [c/layout [edit-form]])

(defn delete-dialog []
  (let [open (subscribe [:articles/delete-dialog-open])
        article (subscribe [:articles/edit])]
    (if @open
      [c/modal {:open      @open
                 :title    (str "删除文章: ")
                 :ok-text  "确认"
                 :on-close #(dispatch [:articles/set-delete-dialog-open false])
                 :on-ok    #(do (dispatch [:articles/set-delete-dialog-open false])
                                (dispatch [:articles/delete (:id @article)]))}
       (str "你确定要删" (:title @article) " 吗?")])))

(defn query-form [{:keys [classes]}]
  (let [query-params (subscribe [:articles/query-params])]
    [:> Form {:name       "query-form"
            :size       "small"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :size      "small"
                      :on-change #(dispatch [:articles/set-query-params :name (-> % .-target .-value)])}]]
     [:div {:style {:text-align "center"}}
      [:> Button {:color    "primary"
                  :size     "small"
                  :variant  "outlined"
                  :type     "reset"
                  :on-click #(dispatch [:articles/clean-query-params])}
       "重置"]
      [:> Button {:variant  "outlined"
                  :size     "small"
                  :color    "primary"
                  :on-click #(dispatch [:articles/load-page @query-params])}
       "搜索"]
      [:> Button {:color    "secondary"
                  :size     "small"
                  :variant  "outlined"
                  :on-click #(navigate! "/articles/add")}
       "新增"]]]))

(defn list-table []
  (let [articles (subscribe [:articles])
        pagination (subscribe [:articles/pagination])
        query-params (subscribe [:articles/query-params])]
    [:<>
     [:> Table {:aria-label    "list-table"
                :size          "small"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell {:align "center"} "序号"]
        [:> Table.HeaderCell {:align "center"} "ID"]
        [:> Table.HeaderCell {:align "center"} "标题"]
        [:> Table.HeaderCell {:align "center"} "简介"]
        [:> Table.HeaderCell {:align "center"} "作者"]
        [:> Table.HeaderCell {:align "center"} "发布状态"]
        [:> Table.HeaderCell {:align "center"} "浏览量"]
        [:> Table.HeaderCell {:align "center"} "创建时间"]
        [:> Table.HeaderCell {:align "center"} "更新时间"]
        [:> Table.HeaderCell {:align "center"} "操作"]
        ]]
      [:> Table.Body
       (let [{:keys [offset per_page]} @pagination]
         (doall
           (for [{:keys [index id title description publish pv create_by create_at update_by update_at] :as article}
                 (map #(assoc %1 :index %2) @articles (range offset (+ offset per_page)))]
             ^{:key article}
             [:> Table.Row
              [:> Table.Cell {:align "center"} (inc index)]
              [:> Table.Cell {:align "center"} id]
              [:> Table.Cell {:align "center"} title]
              [:> Table.Cell {:align "center"} description]
              [:> Table.Cell {:align "center"} publish]
              [:> Table.Cell {:align "center"} pv]
              [:> Table.Cell {:align "center"} create_by]
              [:> Table.Cell {:align "center"} (du/to-date-time create_at)]
              [:> Table.Cell {:align "center"} (du/to-date-time update_at)]
              [:> Table.Cell {:align "center"}
               [:div
                [:> Button {:color    "primary"
                            :size     "small"
                            :icon "edit"
                            :on-click #(navigate! (str "/articles/" id "/edit"))}]

                [:> Button {:color    "secondary"
                            :size     "small"
                            :icon "delete"
                            :style    {:margin "0 8px"}
                            :on-click (fn []
                                        (do
                                          (dispatch [:articles/set-delete-dialog-open true])
                                          (dispatch [:articles/set-attr {:id id :title title}])))}]]]])))]]
     (if @articles
       [c/table-page :articles/load-page (merge @query-params @pagination)])]))

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



