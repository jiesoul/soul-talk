(ns soul-talk.pages.post
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.common :as c]
            [taoensso.timbre :as log])
  (:import [goog.History]))

(defn posts-list []
  (fn []
    (r/with-let
      [posts (subscribe [:admin/posts])]
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
             {:on-click #(dispatch [:posts/delete id])}
             "删除"]]])]])))

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

(defn edit-post-page []
  (r/with-let
    [user (subscribe [:user])
     categories (subscribe [:categories])
     tags (subscribe [:tags])
     error (subscribe [:error])
     original-post (subscribe [:post])
     edited-post (-> @original-post
                   (update :title #(or % ""))
                   (update :content #(or % ""))
                   (update :img_url #(or % ""))
                   (update :category #(or % ""))
                   (update :create_time #(or % nil))
                   (update :modify_time #(or % nil))
                   (update :publish #(or % 0))
                   (update :author #(or % (:name @user)))
                   (update :counter #(or % nil))
                   r/atom)
     post-id (r/cursor original-post [:id])
     content (r/cursor edited-post [:content])
     category (r/cursor edited-post [:category])]
    (fn []
      (when @user
        [:div.container-fluid
         [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
          [:a.navbar-brand
           {:href "#"} "Soul Talk"]
          [:div.container
           [:ul.navbar-nav
            [:li.nav-item.active
             [:a.nav-link
              {:href "#"}
              (if @post-id "修改文章" "写文章")]]]]]
         [:div.container
          [:main#main.col-md-12.ml-sm-auto.col-lg-12.px-4
           [:div
            [:div.form-group
             [c/text-input "" :title "请输入标题" edited-post]]
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
                (if @post-id
                  #(dispatch [:posts/edit @edited-post])
                  #(dispatch [:posts/add @edited-post]))}
               "保存"]]]]]]]))))


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
        (for [{:keys [id title create_time author] :as post} @posts]
          ^{:key post} [:div.blog-post
                        [:h2.blog-post-title
                         [:a.text-muted
                          {:href   (str "/posts/" id)
                           :target "_blank"}
                          title]]
                        [:p.blog-post-meta (str (.toDateString (js/Date. create_time)) " by " author)]
                        [:hr]])]])))