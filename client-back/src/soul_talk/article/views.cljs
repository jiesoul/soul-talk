(ns soul-talk.article.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [soul-talk.routes :refer (navigate!)]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du :refer [to-date]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input TextArea
                                         Dropdown Label]]
            [soul-talk.utils :as utils]))

(defn cancel-button []
  [:> Button {:content "返回"
              :on-click #(navigate! (str "/articles"))}])


(defn new []
  (let [article    (subscribe [:articles/edit])
        tags (:tags article)
        user    (subscribe [:user])
        user-id (:id @user)
        _ (dispatch [:articles/set-attr {:update_by user-id :create_by user-id :publish 0}])
        search (fn [e {:keys [search-query] :as data}]
                 (js/console.log data))]
    [c/layout
     [:> Form
      [:> Form.Input {:name        "name"
                      :placeholder "请输入标题"
                      :required    true
                      :on-change   #(dispatch [:articles/set-attr {:title (utils/event-value %)}])}]
      [:> TextArea {:rows        18
                    :cols        10
                    :placeholder "内容"
                    :on-change   #(dispatch [:articles/set-attr {:body (utils/event-value %)}])}]
      [:div {:style {:margin "5px"}}
       [:> Label.Group
        [:> Label "测试"]
        (doall
          (for [tag tags]
            [:> Label {:content (:name tag)}]))]]

      [:> Dropdown {:placeholder "标签"
                    :multiple true
                    :fluid true
                    :search true
                    :selection true
                    :on-search-change search
                    }]
      [:div.button-center
       [cancel-button]
       [:> Button {:color    "green"
                   :content  "保存"
                   :on-click #(dispatch [:articles/save @article])}]
       [:> Button {:content  "上传文件"
                   :color    "orange"
                   :on-click #()}]]]]))

(defn edit []
  (let [article (subscribe [:articles/edit])
        user (subscribe [:user])
        user-id  (:id @user)
        _ (dispatch [:articles/set-attr {:update_by user-id}])]
    [c/layout
     (let [{:keys [title body]} @article]
       [:> Form
        [:> Form.Input {:name          "name"
                        :required      true
                        :default-value title
                        :on-change     #(let [value (-> % .-target .-value)]
                                          (dispatch [:articles/set-attr {:title value}]))}]
        [:> TextArea {:rows          18
                      :cols          10
                      :placeholder   "内容"
                      :default-value body
                      :on-change     #(dispatch [:articles/set-attr {:body (utils/event-value %)}])}]
        [:div.button-center
         [cancel-button]
         [:> Button {:color "green"
                     :on-click #(dispatch [:articles/update @article])}
          "保存"]]])]))

(defn view []
  [c/layout [:div "ddd"]])

(defn publish-dialog []
  (let [open (subscribe [:articles/publish-dialog])
        article (subscribe [:articles/edit])]
    (if @open
      [c/confirm {:open   @open
                :title    "发布文章"
                :ok-text  "发布"
                :on-close #(dispatch [:articles/set-publish-dialog false])
                :on-ok    #(do
                          (dispatch [:articles/publish (:id @article)])
                          (dispatch [:articles/set-publish-dialog false]))}
       (str "确定发布文章吗？")])))

(defn delete-dialog []
  (let [open (subscribe [:articles/delete-dialog])
        article (subscribe [:articles/edit])]
    (if @open
      [c/confirm {:open    @open
                 :title    (str "删除文章: ")
                 :ok-text  "确认"
                 :on-close #(dispatch [:articles/set-delete-dialog false])
                 :on-ok    #(do (dispatch [:articles/set-delete-dialog false])
                                (dispatch [:articles/delete (:id @article)]))}
       (str "你确定要删 " (:title @article) " 吗?")])))

(defn query-form []
  (let [query-params (subscribe [:articles/query-params])]
    [:> Form {:name       "query-form"}
     [:> Form.Group
      [:> Form.Input {:name      "name"
                      :label     "名称"
                      :size      "small"
                      :inline true
                      :on-change #(dispatch [:articles/set-query-params :name (-> % .-target .-value)])}]]
     [:div.button-center
      [:> Button {:basic true
                  :content "查询"
                  :icon "search"
                  :on-click #(dispatch [:articles/load-page @query-params])}]
      [:> Button {:color    "green"
                  :content "新增"
                  :icon "add"
                  :on-click #(navigate! "/articles/new")}]]]))

(defn list-table []
  (let [articles (subscribe [:articles/list])
        pagination (subscribe [:articles/pagination])
        query-params (subscribe [:articles/query-params])]
    [:<>
     [:> Table {:aria-label    "list-table"
                :celled true
                :text-align "center"}
      [:> Table.Header
       [:> Table.Row
        [:> Table.HeaderCell "序号"]
        [:> Table.HeaderCell "ID"]
        [:> Table.HeaderCell "标题"]
        [:> Table.HeaderCell "简介"]
        [:> Table.HeaderCell "作者"]
        [:> Table.HeaderCell "发布状态"]
        [:> Table.HeaderCell "浏览量"]
        [:> Table.HeaderCell "创建时间"]
        [:> Table.HeaderCell "更新时间"]
        [:> Table.HeaderCell "操作"]
        ]]
      [:> Table.Body
       (let [{:keys [offset per_page]} @pagination]
         (doall
           (for [{:keys [index id title description publish pv create_by create_at update_by update_at] :as article}
                 (map #(assoc %1 :index %2) @articles (range offset (+ offset per_page)))]
             ^{:key article}
             [:> Table.Row
              [:> Table.Cell (inc index)]
              [:> Table.Cell id]
              [:> Table.Cell title]
              [:> Table.Cell description]
              [:> Table.Cell create_by]
              [:> Table.Cell (if (zero? publish) "否" "是")]
              [:> Table.Cell pv]
              [:> Table.Cell (du/to-date-time create_at)]
              [:> Table.Cell (du/to-date-time update_at)]
              [:> Table.Cell
               [:div.button-center
                (when (zero? publish)
                  [:> Button {:content  "发布"
                              :on-click #(do
                                           (dispatch [:articles/set-attr {:id id :title title}])
                                           (dispatch [:articles/set-publish-dialog true]))}])
                [:> Button {:color    "green"
                            :icon "edit"
                            :on-click #(navigate! (str "/articles/" id "/edit"))}]

                [:> Button {:color    "red"
                            :icon "delete"
                            :on-click (fn []
                                        (do
                                          (dispatch [:articles/set-attr {:id id :title title}])
                                          (dispatch [:articles/set-delete-dialog-open true])))}]]]])))]]
     (if @articles
       [c/table-page :articles/load-page (merge @query-params @pagination)])]))

(defn home []
  [c/layout
   [:<>

    [query-form]
    [:> Divider]
    [publish-dialog]
    [delete-dialog]
    [list-table]
    ]])



