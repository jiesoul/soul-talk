(ns soul-talk.pages.post
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.common :as c]
            [re-com.core :refer [input-text single-dropdown]])
  (:import [goog.History]))

(defn posts-list []
  (r/with-let
    [posts (subscribe [:admin/posts])]
    (fn []
      [:table.table.table-striped.text-center.table-hover.table-sm
       [:thead
        [:tr
         [:th "title"]
         [:th "create_time"]
         [:th "modify_time"]
         [:th "publish"]
         [:th "author"]
         [:th "counter"]
         [:th "action"]]]
       [:tbody
        (doall
          (for [{:keys [id title create_time modify_time publish author counter] :as post} @posts]
            ^{:key post}
            [:tr
             [:td title]
             [:td (.toDateString (js/Date. create_time))]
             [:td (.toDateString (js/Date. modify_time))]
             [:td (if (= publish 1) "已发布" "未发布")]
             [:td author]
             [:td (or counter 0)]
             [:td
              [:a.btn.btn-outline-primary.btn-sm.mr-2
               {:target "_blank"
                :href   (str "/posts/" id)}
               "查看"]
              (if (= publish 0)
                [:a.btn.btn-outline-primary.btn-sm.mr-2
                 {:on-click #(dispatch [:posts/publish id])}
                 "发布"])
              [:a.btn.btn-outline-primary.btn-sm.mr-2
               {:target "_blank"
                :href   (str "/posts/" id "/edit")}
               "修改"]
              [:a.btn.btn-outline-primary.btn-sm.mr-2
               {:on-click #(dispatch [:posts/delete id])}
               "删除"]]]))]])))

(defn posts-page []
  (fn []
    [:div.container-fluid
     [:h3 "文章管理"]
     [:hr]
     [:div.p-1
      [:a.btn.btn-outline-primary.btn-sm
       {:target "_blank"
        :href   "/posts/add"}
       "写文章"]]
     [posts-list]
     [c/page-nav :admin/load-posts]]))

(defn add-post-page []
  (r/with-let
    [user (subscribe [:user])
     categories (subscribe [:categories])
     error (subscribe [:error])]
    (let [edited-post (-> {:author  (:name @user)
                           :publish 0}
                        r/atom)
          title       (r/cursor edited-post [:title])
          content     (r/cursor edited-post [:content])
          category    (r/cursor edited-post [:category])]
      [:div.container-fluid
       [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
        [:a.navbar-brand
         {:href "#"} "Soul Talk"]
        [:div.container
         [:ul.navbar-nav
          [:li.nav-item.active
           [:h6.title
            (if @edited-post "修改文章" "写文章")]]]]]
       [:div.container
        [:main#main.col-md-12.ml-sm-auto.col-lg-12.px-4
         [:div
          [:div.form-group
           [:input.form-control.input-lg
            {:type        :text
             :placeholder "请输入标题"
             :value       @title
             :on-change   #(reset! title (-> % .-target .-value))}]]
          [:div.form-inline
           [:div.form-row.col-auto.my-1
            [c/upload-md-modal]]]
          [:div.form-group
           [c/editor content]]
          (when @error
            [:div.alert.alert-danger @error])
          [:div.form-inline
           [:div.form-group
            [:select#category.mr-2.form-control.form-control-sm
             {:on-change    #(reset! category (-> % .-target .-value))
              :defaultValue @category}
             [:option "请选择一个分类"]
             (for [{:keys [id name]} @categories]
               ^{:key id}
               [:option
                {:value id}
                name])]
            [:a.btn.btn-outline-primary.btn-sm.mr-2
             {:on-click
              #(dispatch [:posts/add @edited-post])}
             "保存"]]]]]]])))

(defn edit-post-page []
  (let [user          (subscribe [:user])
        original-post (subscribe [:post])
        error         (subscribe [:error])
        categories    (subscribe [:categories])
        edited-post   (-> @original-post
                        (update :title #(or % ""))
                        (update :content #(or % ""))
                        (update :category #(or % ""))
                        (update :author #(or % (:name @user)))
                        (update :publish #(or % 0))
                        r/atom)
        title         (r/cursor edited-post [:title])
        content       (r/cursor edited-post [:content])
        category      (r/cursor edited-post [:category])
        c-list #(mapv (fn [{:keys [id name]}] {:id id :label name})
                  @categories)]
    (js/console.log @categories)
    (js/console.log c-list)
    [:div.container-fluid
     [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
      [:a.navbar-brand
       {:href "#"} "Soul Talk"]
      [:div.container
       [:ul.navbar-nav
        [:li.nav-item.active
         [:h6.title
          (if @original-post "修改文章" "写文章")]]]]]
     [:div.container
      [:main#main.col-md-12.ml-sm-auto.col-lg-12.px-4
       [:div
        [:div.form-group
         [input-text
          :model title
          :on-change #(reset! title %)
          :placeholder "标题"
          :width "100%"
          :class "form-control input-lg"]]
        [:div.form-inline
         [:div.form-row.col-auto.my-1
          [c/upload-md-modal]]]
        [:div.form-group
         [c/editor content]]
        (when @error
          [:div.alert.alert-danger @error])
        [:div.form-inline
         [:div.form-group
          [single-dropdown
           :model category
           :choices (c-list)
           :placeholder "分类"
           :on-change #(reset! category %)
           :class "mr-2 form-control form-control-sm"]
          [:select#category.mr-2.form-control.form-control-sm
           {:on-change #(reset! category (-> % .-target .-value))
            :value     @category}
           [:option "请选择一个分类"]
           (doall
             (for [{:keys [id name]} @categories]
               ^{:key id}
               [:option
                {:value id}
                name]))]
          [:a.btn.btn-outline-primary.btn-sm.mr-2
           {:on-click
            (if @original-post
              #(dispatch [:posts/edit @edited-post])
              #(dispatch [:posts/add @edited-post]))}
           "保存"]]]]]]]))


(defn post-view-page []
  (r/with-let [post (subscribe [:post])
               user (subscribe [:user])]
    (fn []
      (if @post
        [:div.container
         [:div.text-center
          [:h2.center (:title @post)]]
         [:hr]
         [:div.container
          [c/markdown-preview (:content @post)]]
         [:hr]
         (if @user
           [:div.text-center
            [:a.btn.btn-outline-primary.btn-sm.mr-2
             {:href (str "/posts/" (:id @post) "/edit")}
             "修改文章"]])]))))

(defn post-archives-page []
  (r/with-let [posts (subscribe [:posts])]
    (fn []
      [:div.container-fluid
       [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
        [:a.navbar-brand
         {:href "#"} "Soul Talk"]
        [:div.container
         [:ul.navbar-nav
          [:li.nav-item.active
           [:a.nav-link
            {:href "#"}
            "文章"]]]]]
       [:div.container
        (doall
          (for [{:keys [id title create_time author] :as post} @posts]
            ^{:key post} [:div.blog-post
                          [:h2.blog-post-title
                           [:a.text-muted
                            {:href   (str "/posts/" id)
                             :target "_blank"}
                            title]]
                          [:p.blog-post-meta (str (.toDateString (js/Date. create_time)) " by " author)]
                          [:hr]]))]])))