(ns soul-talk.article.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf :refer [subscribe dispatch]]
            [soul-talk.routes :refer (navigate!)]
            [soul-talk.common.views :as c :refer [logo home-layout manager-layout header]]
            [soul-talk.utils :as du :refer [to-date]]
            [soul-talk.common.md-editor :refer [editor]]
            [antd :as antd]
            ["@ant-design/icons" :as antd-icons]))

(defn home-articles []
  (r/with-let [articles (subscribe [:public-articles])
               loading? (subscribe [:loading?])]
    (fn []
      [:> antd/Skeleton
       {:loading @loading?
        :active true}
       [:> antd/Layout.Content
        [:> antd/Row {:gutter 10}
         (for [{:keys [id title create_at author body] :as article} @articles]
           (let [url (str "/#/articles/" id)]
             ^{:key article}
             [:> antd/Col {:xs 24 :sm 24 :md 8 :lg 8}
              [:> antd/Card {:activeTabKey id
                             :title        (r/as-element
                                             [:div
                                              [:a.text-muted
                                               {:href   url
                                                :target "_blank"}
                                               title]
                                              [:br]
                                              [:span (str (to-date create_at) " by " author)]])
                             :bodyStyle    {:height "220px" :overflow "hidden"}
                             :style        {:margin 5}
                             ;:bordered     false
                             :hoverable    true}
               body]]))]]])))

(defn blog-articles-list [articles]
  [:> antd/List
   {:itemLayout "vertical"
    :size       "small"
    :dataSource @articles
    :renderItem #(let [{:keys [id title body create_at author] :as article} (js->clj % :keywordize-keys true)]
                   (r/as-element
                     [:> antd/List.Item
                      [:> antd/List.Item.Meta
                       {:title       (r/as-element [:a
                                                    {:href   (str "#/articles/" id)
                                                     :target "_blank"}
                                                    [:h2 title]])
                        :description (str " " (to-date create_at) " by " author)}]
                      [c/markdown-preview body]]))}])

(defn blog-articles []
  (r/with-let [articles (subscribe [:public-articles])
               pagination (subscribe [:home-pagination])]
    (when @articles
      (fn []
        (let [edited-pagination (-> @pagination
                                  r/atom)
              page (r/cursor edited-pagination [:page])
              pre-page (r/cursor edited-pagination[:pre-page])
              total (r/cursor edited-pagination [:total])]
          [:> antd/Card
           {:title "文章列表"}
           [:div
            [blog-articles-list articles]
            (when (pos? @total)
              [:> antd/Row {:style {:text-align "center"}}
               [:> antd/Pagination {:current   @page
                                    :pageSize  @pre-page
                                    :total     @total
                                    :on-change #(do (reset! page %1)
                                                    (reset! pre-page %2)
                                                    (dispatch [:load-articles @edited-pagination]))}]])]])))))

(defn blog-archives-articles []
  (r/with-let [articles (subscribe [:public-articles])]
    (when @articles
      (fn []
        [:> antd/Card
         {:title "文章列表"}
         [:> antd/Layout.Content
          [blog-articles-list articles]]]))))

(defn blog-archives []
  (r/with-let [articles-archives (subscribe [:public-articles-archives])]
    (when @articles-archives
      (fn []
        [:> antd/Card
         {:title "文章归档"}
         [:> antd/List
          {:itemLayout "vertical"
           :dataSource @articles-archives
           :renderItem (fn [article]
                         (let [{:keys [year month counter :as article]} (js->clj article :keywordize-keys true)
                               title (str year "年 " month " 月 (" counter ")")]
                           (r/as-element
                             [:> antd/List.Item
                              [:div
                               [:a
                                {:on-click #(navigate! (str "#/blog/archives/" year "/" month))}
                                title]]])))}]]))))

(defn list-columns []
  [{:title "标题" :dataIndex "title", :key "title", :align "center"}
   {:title  "创建时间" :dataIndex "create_at" :key "create_at" :align "center"
    :render (fn [_ article]
              (let [article (js->clj article :keywordize-keys true)]
                (du/to-date-time (:create_at article))))}
   {:title  "更新时间" :dataIndex "update_at" :key "update_at" :align "center"
    :render (fn [_ article]
              (let [article (js->clj article :keywordize-keys true)]
                (du/to-date-time (:update_at article))))}
   {:title "发布状态" :dataIndex "publish" :key "publish" :align "center"}
   {:title "作者" :dataIndex "author" :key "author" :align "center"}
   {:title "浏览量" :dataIndex "counter" :key "counter" :align "center"}
   {:title  "操作" :dataIndex "actions" :key "actions" :align "center"
    :render (fn [_ article]
              (r/as-element
                (let [{:keys [id publish]} (js->clj article :keywordize-keys true)]
                  [:div
                   [:> antd/Button {:size   "small"
                                    :target "_blank"
                                    :href   (str "#/articles/" id)}
                    "查看"]
                   [:> antd/Divider {:type "vertical"}]
                   [:> antd/Button {:icon (r/as-element [:> antd-icons/EditOutlined])
                                    :size   "small"
                                    :target "_blank"
                                    :href   (str "#/articles/" id "/edit")}]
                   [:> antd/Divider {:type "vertical"}]
                   [:> antd/Button {:type     "danger"
                                    :icon     (r/as-element [:> antd-icons/DeleteOutlined])
                                    :size     "small"
                                    :on-click (fn []
                                                (r/as-element
                                                  (c/show-confirm
                                                    "文章删除"
                                                    (str "你确认要删除这篇文章吗？")
                                                    #(dispatch [:articles/delete id])
                                                    #(js/console.log "cancel"))))}]
                   [:> antd/Divider {:type "vertical"}]
                   (when (zero? publish)
                     [:> antd/Button {:type     "primary"
                                      :size     "small"
                                      :on-click #(dispatch [:articles/publish id])}
                      "发布"])])))}])

(defn articles-list []
  (r/with-let [articles (subscribe [:articles])]
    (fn []
      [:div
       [:> antd/Table {:columns    (clj->js (list-columns))
                       :dataSource (clj->js @articles)
                       :row-key    "id"
                       :bordered   true
                       :size       "small"}]])))

(defn articles-page []
  [manager-layout
   [:> antd/Layout.Content {:className "main"}
    [:> antd/Button
     {:target "_blank"
      :href   "#/articles/add"
      :size   "small"}
     "写文章"]
    [:> antd/Divider]
    [articles-list]]])

(defn edit-menu []
  (r/with-let [article (rf/subscribe [:editing-article])]
    [:<>
     [:> antd/Col {:span 1}
      [:> antd/Divider {:type "vertical"}]]
     [:> antd/Col {:span 15}
      [:h2 "写文章"]]
     [:> antd/Col {:span 4}
      [:> antd/Button
       "保存"]]]))

(defn article-layout [main]
  [:> antd/Layout
   [header edit-menu]
   [:> antd/Layout.Content
    main]])

(defn add-article-page []
  (r/with-let [user (rf/subscribe [:user])]
    (fn []
      (let [edited-article (r/atom {:title ""
                                    :body ""
                                    :description "sssss"
                                    :create_by (:id @user)
                                    :publish 0
                                    :counter 0
                                    :create_at (js/Date.now)})
            body     (r/cursor edited-article [:body])
            title       (r/cursor edited-article [:title])]

        [article-layout
         [:> antd/Form
          [:> antd/Row
           [:> antd/Col {:span 16 :offset 4 :style {:padding-top "10px"}}
            [:> antd/Input
             {:on-change   #(let [val (-> % .-target .-value)]
                              (reset! title val)
                              (dispatch [:articles/add @edited-article]))
              :placeholder "请输入标题"}]]]
          [:> antd/Row
           [:> antd/Col {:span 16 :offset 4}
            [editor body]]]]]))))

(defn edit-article-page []
  (r/with-let [article (subscribe [:article])
               user (subscribe [:user])
               tags (subscribe [:tags])]
    (fn []
      (let [edited-article (-> @article
                             (update :id #(or % nil))
                             (update :title #(or % nil))
                             (update :body #(or % nil))
                             (update :category #(or % nil))
                             (update :author #(or % (:name @user)))
                             (update :publish #(or % 0))
                             (update :counter #(or % 0))
                             (update :create_at #(or % (js/Date.)))
                             r/atom)
            body     (r/cursor edited-article [:body])
            title       (r/cursor edited-article [:title])]
        (if-not @article
          [:div [:> antd/Spin {:tip "loading"}]]
          [:> antd/Form
           [article-layout
            [:> antd/Layout.Content
             [:> antd/Row
              [:> antd/Col {:span 16 :offset 4 :style {:padding-top "10px"}}
               [:> antd/Input
                {:on-change    #(let [val (-> % .-target .-value)]
                                  (reset! title val))
                 :placeholder  "请输入标题"
                 :size         "large"
                 :defaultValue @title}]
               [:> antd/Divider]]]
             [:> antd/Row
              [:> antd/Col {:span 12 :offset 4}
               [:> antd/Input.Textarea {:row 4}]
               ;[editor body]
               ]]]]])))))


(defn article-view-page []
  (r/with-let [article (subscribe [:article])
               user (subscribe [:user])]
    (fn []
      (if @article
        [:div.article-view
         [:> antd/Card
          [:div
           [:> antd/Typography.Title {:style {:text-align "center"}}
            (:title @article)]
           [:div
            {:style {:text-align "center"}}
            (str (to-date (:create_at @article)) " by " (:author @article))]
           [:> antd/Divider]
           [:> antd/Typography.Text
            [c/markdown-preview (:body @article)]]]]]))))

(defn article-archives-page []
  (r/with-let [articles (subscribe [:articles])]
    (fn []
      [:div
       (doall
         (for [{:keys [id title create_at author] :as article} @articles]
           ^{:key article} [:div.blog-article
                            [:h2.blog-article-title
                             [:a.text-muted
                              {:href   (str "/articles/" id)
                               :target "_blank"}
                              title]]
                            [:p.blog-article-meta (str (.toDateString (js/Date. create_at)) " by " author)]
                            [:hr]]))])))

(defn article-errors [article]
  (->
    first
    (vals)))

(defn category-select [category categories]
  [:> antd/Select {:value        {:key @category}
                   :labelInValue true
                   :style        {:width 120 :padding "5px"}
                   :on-change    #(let [val (:key (js->clj % :keywordize-keys true))]
                                    (reset! category val))}
   [:> antd/Select.Option {:value ""} "选择分类"]
   (doall
     (for [{:keys [id name]} @categories]
       ^{:key id} [:> antd/Select.Option {:value id} name]))])



