(ns soul-talk.article.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [soul-talk.routes :refer (navigate!)]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du :refer [to-date]]
            ["semantic-ui-react" :refer [Form Button Table Divider Icon Container Card Input TextArea]]
            [soul-talk.utils :as utils]))

(defn view []
  [:div "ddd"])

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
  (let [articles (subscribe [:articles])
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
              [:> Table.Cell publish]
              [:> Table.Cell pv]
              [:> Table.Cell (du/to-date-time create_at)]
              [:> Table.Cell (du/to-date-time update_at)]
              [:> Table.Cell
               [:div.button-center
                [:> Button {:content "发布"
                            :on-click #(do
                                         (dispatch [:articles/set-attr {:id id :title title}])
                                         (dispatch [:articles/set-publish-dialog true]))}]
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



