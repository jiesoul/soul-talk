(ns soul-talk.common.views
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            [antd :as antd :refer [Modal Menu Row Col Layout Footer]]
            ["@ant-design/icons" :as antd-icons]
            ["react-highlight.js" :as hljs]
            [soul-talk.routes :refer [navigate!]]))

(defn logo []
  (let [site-info (rf/subscribe [:site-info])]
    [:div.logo
     [:h1
      (:name @site-info)]]))

(defn manager-breadcrumb []
  (r/with-let [items (rf/subscribe [:breadcrumb])]
    [:> antd/Breadcrumb {:className "site-breadcrumb"}
     (for [item @items]
       ^{:key item}
       [:> antd/Breadcrumb.Item item])]))

(defn manager-user-nav []
  (r/with-let [user (rf/subscribe [:user])]
    [:> antd/Menu
     [:> antd/Menu.Item {:key      "user-profile"
                         :icon     (r/as-element [:> antd-icons/UserOutlined])
                         :on-click #(navigate! "#/user/profile")}
      "个人信息"]
     [:> antd/Menu.Item {:key      "change-pass"
                         :icon     (r/as-element [:> antd-icons/EditOutlined])
                         :on-click #(navigate! "#/user/password")}

      "密码修改"]
     [:> antd/Menu.Divider]
     [:> antd/Menu.Item {:key      "cancel"
                         :on-click #(rf/dispatch [:logout @user])
                         :icon     (r/as-element [:> antd-icons/LoginOutlined])}
      "退出登录"]]))

(defn header [nav]
  [:> antd/Layout.Header {:className "site-layout-header"}
   [:> antd/Row {:justify "left"}
    [:> antd/Col {:xs 24 :sm 24 :md 8 :lg 8}
     [logo]]
    [:> antd/Col {:xs 24 :sm 24 :md 16 :lg 16}
     [nav]]]])

(defn home-row-col [component]
  [:> antd/Row {:justify "center" :align "middle"}
   [:> antd/Col {:xs 24 :sm 24 :md 24 :lg 24}
    component]])

(defn footer []
  (let [site-info (rf/subscribe [:site-info])]
    [:> antd/Layout.Footer {:className "site-layout-footer"}
     [home-row-col
      [:section
       [:h4
        "Made with By "
        [:a
         {:type   "link"
          :href   "https://ant.design"
          :target "_blank"}
         "Ant Design"]
        " and  " (:author @site-info)]]]]))

(defn sidebar []
  (r/with-let [active-page (rf/subscribe [:active-page])
               menus (rf/subscribe [:menus])]
    (fn []
      [:> antd/Layout.Sider {:className "site-layout-sidebar"}
       [:> antd/Menu {:mode                "inline"
                      :style               {:height "100%" :borderRight 0}
                      :default-select-keys ["10"]
                      :open-keys           ["base" "user" "article"]
                      :selected-keys       [(key->js @active-page)]}

        [:> antd/Menu.Item {:key      "dash"
                            :icon     (r/as-element [:> antd-icons/DashboardOutlined])
                            :on-click #(navigate! "#/dash")} "数据面板"]

        [:> antd/Menu.SubMenu {:key "article" :title "文章管理"}
         [:> antd/Menu.Item {:key "articles" :on-click #(navigate! "#/articles")} "文章"]]
        [:> antd/Menu.SubMenu {:key   "base"
                               :title "基础数据"}
         [:> antd/Menu.Item {:key "series" :on-click #(navigate! "#/series")} "系列管理"]
         [:> antd/Menu.Item {:key "tag" :on-click #(navigate! "#/tags")} "标签管理"]
         [:> antd/Menu.Item {:key "data-dic" :on-click #(navigate! "#/data-dices")} "数据字典"]
         [:> antd/Menu.Item {:key "app-key" :on-click #(navigate! "#/app-keys")} "APP Key管理"]

         [:> antd/Menu.Item {:key "site-info" :on-click #(navigate! "#/site-info/1")} "网站基础信息"]

         [:> antd/Menu.Item {:key "menu" :on-click #(navigate! "#/menus")} "菜单管理"]
         ]

        ]])))



(defn manager-header-dropdown []
  (r/with-let [user (rf/subscribe [:user])]
    [:div {:style {:text-align "right"}}
     [:> antd/Dropdown {:overlay (r/as-element [manager-user-nav])}
      [:a {:className "ant-dropdown-link"
           :href      "#"}
       [:> antd-icons/UserOutlined]
       "  " (:name @user)]]]))

(defn manager-layout [main]
  [:> antd/Layout
   [header manager-header-dropdown]
   [:> antd/Layout
    [sidebar]
    [:> antd/Layout {:style {:padding "0 24px 24px"}}
     [manager-breadcrumb]
     [:> antd/Layout.Content {:className "site-layout-content"}
      main]]]
   [footer]])

(defn page-nav [handler]
  (r/with-let
    [pagination (rf/subscribe [:pagination])
     prev-page (r/cursor pagination [:previous])
     next-page (r/cursor pagination [:next])
     page (r/cursor pagination [:page])
     per-page (r/cursor pagination [:per-page])
     total-pages (r/cursor pagination [:total-pages])
     total (r/cursor pagination [:total])
     paginate-params @pagination]
    (fn []
      (let [start (max 1 (- @page 5))
            end (inc (min @total-pages (+ @page 5)))]
        [:nav
         [:ul.pagination.justify-content-center.pagination-sm
          [:li.page-item
           {:class (if (= @page 1) "disabled")}
           [:a.page-link
            {:on-click  #(rf/dispatch [handler (assoc paginate-params :page @prev-page)])
             :tab-index "-1"}
            "Previous"]]
          (doall
            (for [p (range start end)]
              ^{:key p}
              [:li.page-item
               {:class (if (= p @page) "active")}
               [:a.page-link
                {:on-click #(rf/dispatch [handler (assoc paginate-params :page p)])}
                p]]
              ))
          [:li.page-item
           {:class (if (> @next-page @total-pages) "disabled")}
           [:a.page-link
            {:on-click #(rf/dispatch [handler (assoc paginate-params :page @next-page)])}
            "Next"]]]]))))

(defn admin [page]
  (r/with-let [user (rf/subscribe [:user])]
    (if @user
      [page]
      (navigate! "#/login"))))

(defn loading-modal []
  (r/with-let [loading? (rf/subscribe [:loading?])]
    (fn []
      (when @loading?
        [:> antd/Spin {:tip  "加载中。。。。"
                       :size "large"}]))))

(defn spin-loading []
  (r/with-let [loading? (rf/subscribe [:loading?])]
    (when @loading?
      (antd/message.loading "正在加载中。。。。"))))

(defn show-confirm
  [title content ok-fun cancel-fun]
  (antd/Modal.confirm
    (clj->js {:centered true
              :title    title
              :content  content
              :onOk     ok-fun
              :onCancel cancel-fun})))

(defn success-message []
  (r/with-let [success (rf/subscribe [:success])]
    (when @success
      (antd/message.success @success)
      (rf/dispatch [:clean-success]))))

(defn error-message []
  (r/with-let [error (rf/subscribe [:error])]
    (when @error
      (antd/message.error @error)
      (rf/dispatch [:clean-error]))))


(defn form-modal [title content visible success-fn cancel-fn]
  [:> antd/Modal
   {:title    title
    :visible  visible
    :onOk     success-fn
    :onCancel cancel-fn}
   content])

(defn show-modal
  [modal-option content]
  [:> Modal modal-option
   content])

(defn validation-modal [title errors]
  [:> antd/Modal {:is-open (boolean @errors)}
   [:> antd/ModalHeader title]
   [:> antd/ModalBody
    [:ul
     (doall
       (for [[_ error] @errors]
         ^{:key error}
         [:li error]))]]
   [:> antd/ModalFooter
    [:button.btn.btn-sm.btn-danger
     {:on-click #(reset! errors nil)}
     "Close"]]])

;;高亮代码 循环查找结节
(defn highlight-code [node]
  (let [nodes (.querySelectorAll (rd/dom-node node) "pre code")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock hljs/hljs item))
        (recur (dec i))))))

;; 处理 markdown 转换
(defn markdown-preview []
  (let [md-parser (js/showdown.Converter.)]
    (r/create-class
      {:component-did-mount
       #(highlight-code (rd/dom-node %))
       :component-did-update
       #(highlight-code (rd/dom-node %))
       :reagent-render
       (fn [content]
         [:div
          {:dangerouslySetInnerHTML
           {:__html (.makeHtml md-parser (str content))}}])})))

