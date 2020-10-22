(ns soul-talk.common.layout
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.common.component :refer [header header-dropdown footer]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.common :as c]
            [antd]))

(defn layout [children]
  [:> antd/Layout
   [:> antd/Layout.Content
    children]
   [:> antd/Divider]
   [footer]])

(defn nav []
  [:> antd/Menu
   [:> antd/Menu.Item {:key "user-profile"
                          :on-click #(navigate! "#/user-profile")}
    [:> antd/Icon {:type "user"}]
    "个人信息"]
   [:> antd/Menu.Item {:key "change-pass"
                          :on-click #(navigate! "#/change-pass")}
    [:> antd/Icon {:type "setting"}]
    "密码修改"]
   [:> antd/Menu.Divider]
   [:> antd/Menu.Item {:key      "cancel"
                          :on-click #(dispatch [:logout])}
    [:> antd/Icon {:type "poweroff"}]
    "退出登录"]])

(defn sidebar [active-page]
  [:> antd/Layout.Sider {:className "sidebar"}
   [:> antd/Menu {:mode                "inline"
                     :className            "sidebar"
                     :theme               "light"
                     :default-select-keys ["dash"]
                     :default-open-keys   ["tags" "articles" "users"]
                     :selected-keys       [(key->js @active-page)]}
    [:> antd/Menu.Item {:key      "dash"
                           :on-click #(navigate! "#/dash")}
     [:span
      [:> antd/Icon {:type "area-chart"}]
      [:span "Dash"]]]
    [:> antd/Menu.SubMenu {:key   "tags"
                              :title (r/as-element [:span
                                                    [:> antd/Icon {:type "form"}]
                                                    [:span "标签管理"]])}
     [:> antd/Menu.Item {:key      "tags-list"
                            :on-click #(navigate! "#/tags")}
      "分类"]]
    [:> antd/Menu.SubMenu {:key   "articles"
                              :title (r/as-element [:span
                                                    [:> antd/Icon {:type "form"}]
                                                    [:span "文章管理"]])}
     [:> antd/Menu.Item {:key      "articles-list"
                            :on-click #(navigate! "#/articles")}
      "文章"]]

    [:> antd/Menu.SubMenu {:key   "user"
                              :title (r/as-element
                                       [:span
                                        [:> antd/Icon {:type "user"}]
                                        [:span "个人管理"]])}
     [:> antd/Menu.Item {:key      "user-profile"
                            :icon     "user"
                            :on-click #(navigate! "#/user/profile")}
      "个人信息"]
     [:> antd/Menu.Item {:key      "change-pass"
                            :on-click #(navigate! "#/user/password")} "密码修改"]
     ]]])

(defn basic-layout [main]
  (r/with-let [user (subscribe [:user])
               active-page (subscribe [:active-page])
               breadcrumb (subscribe [:breadcrumb])]
    (fn []
      [:div.admin
       [:> antd/Layout
        [header
         [header-dropdown (r/as-element [nav]) (:name @user)]]
        [:> antd/Layout
         [sidebar active-page]
         [:> antd/Layout.Content {:className "main"}
          [:div
           [c/breadcrumb-component @breadcrumb]
           [:hr]
           main]]]
        [footer]]])))

