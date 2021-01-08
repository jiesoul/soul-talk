(ns soul-talk.article.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [soul-talk.routes :refer (navigate!)]
            [soul-talk.common.views :as c]
            [soul-talk.utils :as du :refer [to-date]]
            [soul-talk.common.md-editor :refer [editor]]
            [soul-talk.common.styles :as styles]
            ["@material-ui/core" :as mui :refer [Button Divider Table Form Row Col Input Layout Header ]]
            ["@material-ui/icons" :as mui-icons]))
;
;(def list-columns
;  [{:title "标题" :dataIndex "title", :key "title", :align "center"}
;   {:title  "创建时间" :dataIndex "create_at" :key "create_at" :align "center"
;    :render (fn [_ article]
;              (let [article (js->clj article :keywordize-keys true)]
;                (du/to-date-time (:create_at article))))}
;   {:title  "更新时间" :dataIndex "update_at" :key "update_at" :align "center"
;    :render (fn [_ article]
;              (let [article (js->clj article :keywordize-keys true)]
;                (du/to-date-time (:update_at article))))}
;   {:title "发布状态" :dataIndex "publish" :key "publish" :align "center"}
;   {:title "作者" :dataIndex "create_by" :key "create_by" :align "center"}
;   {:title "浏览量" :dataIndex "pv" :key "pv" :align "center"}
;   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"
;    :render (fn [_ article]
;              (r/as-element
;                (let [{:keys [id publish]} (js->clj article :keywordize-keys true)]
;                  [:div
;                   [:> Button {:size   "small"
;                               :target "_blank"
;                               :href   (str "#/articles/" id)}
;                    "查看"]
;                   [:> Divider {:type "vertical"}]
;                   [:> Button {:icon   (r/as-element [:> mui-icons/Edit])
;                               :size   "small"
;                               :target "_blank"
;                               :href   (str "#/articles/" id "/edit")}]
;                   [:> Divider {:type "vertical"}]
;                   [:> Button {:type     "danger"
;                               :icon     (r/as-element [:> mui-icons/Delete])
;                               :size     "small"
;                               :on-click (fn []
;                                           (r/as-element
;                                             (c/show-confirm
;                                               "文章删除"
;                                               (str "你确认要删除这篇文章吗？")
;                                               #(dispatch [:articles/delete id])
;                                               #(js/console.log "cancel"))))}]
;                   [:> Divider {:type "vertical"}]
;                   (when (zero? publish)
;                     [:> Button {:type     "primary"
;                                 :size     "small"
;                                 :on-click #(dispatch [:articles/publish id])}
;                      "发布"])])))}])
;
;(defn query-list []
;  (r/with-let [articles (subscribe [:articles])]
;    (fn []
;      [:div.search-result-list
;       [:> Table {:columns    (clj->js list-columns)
;                  :dataSource (clj->js @articles)
;                  :row-key    "id"
;                  :bordered   true
;                  :size       "small"}]])))
;
;
;(defn query-form []
;  (let [pagination (subscribe [:pagination])
;        params (r/atom nil)]
;    (fn []
;      (let [name (r/cursor params [:name])]
;        [:div.advanced-search-form
;         [:> Form {:title     ""
;                   :className "advanced-search-form"}
;          [:> Row {:gutter 24}
;           [:> Col {:span 8}
;            [:> Form.Item {:name  "name"
;                           :label "name"}
;             [:> Input {:placeholder "name"
;                        :value       @name
;                        :on-blur     #(reset! name (-> % .-target .-value))}]]]]
;          [:> Row
;           [:> Col {:span 24 :style {:text-align "right"}}
;            [:div
;             [:> Button {:type     "primary"
;                         :htmlType "submit"
;                         :on-click #(dispatch [:tags/load-all (merge @params @pagination)])}
;              "search"]
;             [:> Button {:target "_blank"
;                         :style {:margin "0 8px"}
;                         :href   "#/articles/add"}
;              "写文章"]]]]]]))))
;
;(defn query-page [props]
;  [c/layout props
;   [:<>
;    [query-form]
;    [query-list]]])
;
;(defn home []
;  (styles/main query-page))
;
;(defn edit-menu []
;  (r/with-let [article (rf/subscribe [:editing-article])]
;    [:<>
;     [:> Col {:span 1}
;      [:> Divider {:type "vertical"}]]
;     [:> Col {:span 15}
;      [:h2 "写文章"]]
;     [:> Col {:span 4}
;      [:> Button
;       "保存"]]]))
;
;(defn article-layout [main]
;  [:> Layout
;   [header edit-menu]
;   [:> Layout.Content
;    main]])
;
;(defn add-article-page []
;  (r/with-let [user (rf/subscribe [:user])]
;    (fn []
;      (let [edited-article (r/atom {:title ""
;                                    :body ""
;                                    :description "sssss"
;                                    :create_by (:id @user)
;                                    :publish 0
;                                    :counter 0
;                                    :create_at (js/Date.now)})
;            body     (r/cursor edited-article [:body])
;            title       (r/cursor edited-article [:title])]
;
;        [article-layout
;         [:> Form
;          [:> Row
;           [:> Col {:span 16 :offset 4 :style {:padding-top "10px"}}
;            [:> Input
;             {:on-change   #(let [val (-> % .-target .-value)]
;                              (reset! title val)
;                              (dispatch [:articles/add @edited-article]))
;              :placeholder "请输入标题"}]]]
;          [:> Row
;           [:> Col {:span 16 :offset 4}
;            [editor body]]]]]))))
;
;(defn edit-article-page []
;  (r/with-let [article (subscribe [:article])
;               user (subscribe [:user])
;               tags (subscribe [:tags])]
;    (fn []
;      (let [edited-article (-> @article
;                             (update :id #(or % nil))
;                             (update :title #(or % nil))
;                             (update :body #(or % nil))
;                             (update :category #(or % nil))
;                             (update :author #(or % (:name @user)))
;                             (update :publish #(or % 0))
;                             (update :counter #(or % 0))
;                             (update :create_at #(or % (js/Date.)))
;                             r/atom)
;            body     (r/cursor edited-article [:body])
;            title       (r/cursor edited-article [:title])]
;        (if-not @article
;          [:div [:> Spin {:tip "loading"}]]
;          [:> Form
;           [article-layout
;            [:> Layout.Content
;             [:> Row
;              [:> Col {:span 16 :offset 4 :style {:padding-top "10px"}}
;               [:> Input
;                {:on-change    #(let [val (-> % .-target .-value)]
;                                  (reset! title val))
;                 :placeholder  "请输入标题"
;                 :size         "large"
;                 :defaultValue @title}]
;               [:> Divider]]]
;             [:> Row
;              [:> Col {:span 12 :offset 4}
;               [:> Input.Textarea {:row 4}]
;               ;[editor body]
;               ]]]]])))))
;
;(defn article-view-page []
;  (r/with-let [article (subscribe [:article])
;               user (subscribe [:user])]
;    (fn []
;      (if @article
;        [:div.article-view
;         [:> Card
;          [:div
;           [:> Typography.Title {:style {:text-align "center"}}
;            (:title @article)]
;           [:div
;            {:style {:text-align "center"}}
;            (str (to-date (:create_at @article)) " by " (:author @article))]
;           [:> Divider]
;           [:> Typography.Text
;            [c/markdown-preview (:body @article)]]]]]))))



