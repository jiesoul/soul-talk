(ns soul-talk.pages.post
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.common :as c]
            [cljs-time.format :as f]
            [cljsjs.simplemde]
            [taoensso.timbre :as log])
  (:import [goog.History]))

(defn posts-list []
  (r/with-let [posts (subscribe [:admin/posts])]
    [:table.table.table-striped
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
         [:td publish]
         [:td author]
         [:td counter]
         [:td
          [:a.btn.btn-outline-primary.btn-sm.mr-2
           {:target "_blank"
            :href   (str "/posts/" id)}
           "查看"]
          [:a.btn.btn-outline-primary.btn-sm.mr-2
           {:on-click #(dispatch [:posts/publish id])}
           "发布"]
          [:a.btn.btn-outline-primary.btn-sm.mr-2
           {:on-click #(dispatch [:posts/delete id])}
           "删除"]]])]]))

(defn posts-page []
  (r/with-let [user (subscribe [:user])]
    [:div.container-fluid
     [:h3 "文章管理"]
     [:hr]
     [:div.p-1
      [:a.btn.btn-outline-primary.btn-sm
       {:target "_blank"
        :href   "/posts/add"}
       "写文章"]]
     [posts-list]]))


(defn create-post-page []
  (r/with-let [user (subscribe [:user])
               categories (subscribe [:categories])
               tags (subscribe [:tags])
               error (subscribe [:error])
               post (r/atom {:author (:name @user)
                             :publish 0})
               content (r/cursor post [:content])]
              (if @user
                [:main#main.col-md-12.ml-sm-auto.col-lg-12.px-4
                 [:div
                  [:div.form-group
                   [c/text-input "" :title "请输入标题" post]]
                  [c/editor content]
                  (when @error
                    [:div.alert.alert-danger @error])
                  [:div.form-inline
                   [:div.form-group
                    [:label.p-1 {:for "category"} "分类:"]
                    [:select#category.mr-2.form-control.form-control-sm
                     {:on-change   #(swap! post assoc :category (-> % .-target .-value))
                      :placeholder "请选择一个分类"}
                     [:option ""]
                     (for [{:keys [id name]} @categories]
                       ^{:key id}
                       [:option {:value id} name])]
                    [:a.btn.btn-outline-primary.btn-sm.mr-2
                     {:on-click #(dispatch [:posts/add @post])}
                     "保存"]]]]])))

(defn edit-post-page []
  (r/with-let [user (subscribe [:user])
                categories (subscribe [:categories])
                tags (subscribe [:tags])
                error (subscribe [:error])
                post (subscribe [:post])
               content (r/cursor post [:content])]
              (if @user
                [:main#main.col-md-12.ml-sm-auto.col-lg-12.px-4
                 [:div
                  [:div.form-group
                   [c/text-input "" :title "请输入标题" post]]
                  [c/editor content]
                  (when @error
                    [:div.alert.alert-danger @error])
                  [:div.form-inline
                   [:div.form-group
                    [:label.p-1 {:for "category"} "分类:"]
                    [:select#category.mr-2.form-control.form-control-sm
                     {:on-change   #(swap! post assoc :category (-> % .-target .-value))
                      :placeholder "请选择一个分类"
                      :defaultValue (:category @post)}
                     [:option ""]
                     (for [{:keys [id name]} @categories]
                       ^{:key id}
                       [:option
                        {:value id}
                        name])]
                    [:a.btn.btn-outline-primary.btn-sm.mr-2
                     {:on-click #(dispatch [:posts/edit @post])}
                     "保存"]]]]])))


(defn post-view-page []
  (r/with-let [post (subscribe [:post])
                user (subscribe [:user])]
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
                     {:href   (str "/posts/" (:id @post) "/edit")}
                     "修改文章"]])])))