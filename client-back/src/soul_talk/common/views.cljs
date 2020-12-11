(ns soul-talk.common.views
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [reagent.impl.template :as rtpl]
            [re-frame.core :as rf]
            [antd :as antd :refer [Breadcrumb Modal Menu Row Col Layout Footer]]
            ["@ant-design/icons" :refer [UserOutlined EditOutlined LoginOutlined DashboardOutlined]]
            ["react-highlight.js" :as hljs]
            ["@material-ui/core" :as mui]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]))

(def validate-messages {:required "${label} 必须的"
                        :types {:email "${label} 非法邮件格式"
                                :url "${label} 非法地址"}})

(def ^:private input-component
  (r/reactify-component
    (fn [props]
      [:input (-> props
                (assoc :ref (:inputRef props))
                (dissoc :inputRef))])))

(def ^:private textarea-component
  (r/reactify-component
    (fn [props]
      [:textarea (-> props
                   (assoc :ref (:inputRef props))
                   (dissoc :inputRef))])))

(defn text-field [props & children]
  (let [props (-> props
                (assoc-in [:InputProps :inputComponent] (cond
                                                          (and (:multiline props) (:rows props) (not (:maxRows props)))
                                                          textarea-component

                                                          ;; FIXME: Autosize multiline field is broken.
                                                          (:multiline props)
                                                          nil

                                                          ;; Select doesn't require cursor fix so default can be used.
                                                          (:select props)
                                                          nil

                                                          :else
                                                          input-component))
                rtpl/convert-prop-value)]
    (apply r/create-element mui/TextField props (map r/as-element children))))

(defn copyright []
  (let [year (.getFullYear (js/Date.))]
    [:> mui/Typography {:variant "body2"
                        :color   "textSecondary"
                        :align   "center"}
     "Copyright ©"
     [:> mui/Link {:color "inherit"
                   :href  "https://www.jiesoul.com/"}
      "jiesoul"]
     (str " " year ".")
     ]))

(def ^:dynamic *open* (r/atom true))
(defn handle-drawer-open []
  (reset! *open* true))

(defn handle-drawer-close []
  (reset! *open* false))

(defn layout [{:keys [classes] :as props} component]
  [:div {:class-name (.-root classes)}
   [:> mui/CssBaseline]
   [:> mui/AppBar {:position   "absolute"
                   :class-name (.-appBar classes)}
    [:> mui/Toolbar {:class-name (.-toolbar classes)}
     [:> mui/IconButton {:edge       "start"
                         :color      "inherit"
                         :aria-label "open drawer"
                         :on-click   #()
                         :class-name ""}

      ]]
    [:> mui/Typography {:component  "h1"
                        :variant    "h6"
                        :color      "inherit"
                        :no-wrap    true
                        :class-name (.-title classes)}
     "Dashboard"]
    [:> mui/IconButton {:color "inherit"}
     [:> mui/Badge {:badge-content 4
                    :color         "secondary"}
      ]]]

   [:> mui/Drawer {:variant "permanent"
                   :open    "open"}
    [:div
     [:> mui/IconButton {:on-click #()}]]
    [:> mui/Divider]]

   [:main {:class-name (.-content classes)}
    [:div {:class-name (.-appBarSpacer classes)}]
    [:> mui/Container {:max-width  "lg"
                       :class-name (.-container classes)}

     [:> mui/Box {:pt 4}
      [copyright]]]]
   ])

(defn logo []
  (let [site-info (rf/subscribe [:site-info])]
    [:div.logo
     [:h1
      (:name @site-info)]]))

(defn manager-breadcrumb []
  (let [items (rf/subscribe [:breadcrumb])]
    (fn []
      [:> Breadcrumb {:className "site-breadcrumb"}
       (for [item @items]
         ^{:key item}
         [:> Breadcrumb.Item item])])))

(defn manager-user-nav []
  (let [user (rf/subscribe [:user])]
    (fn []
      [:> Menu
       [:> Menu.Item {:key      "user-profile"
                      :icon     (r/as-element [:> UserOutlined])
                      :on-click #(navigate! "#/users/profile")}
        "个人信息"]
       [:> Menu.Item {:key      "change-pass"
                      :icon     (r/as-element [:> EditOutlined])
                      :on-click #(navigate! "#/users/password")}

        "密码修改"]
       [:> Menu.Divider]
       [:> Menu.Item {:key      "cancel"
                      :on-click #(rf/dispatch [:logout @user])
                      :icon     (r/as-element [:> LoginOutlined])}
        "退出登录"]])))

(defn header [nav]
  [:> Layout.Header {:className "site-layout-header"}
   [:> Row {:justify "left"}
    [:> Col {:xs 24 :sm 24 :md 8 :lg 8}
     [logo]]
    [:> Col {:xs 24 :sm 24 :md 16 :lg 16}
     [nav]]]])

(defn home-row-col [component]
  [:> Row {:justify "center" :align "middle"}
   [:> Col {:xs 24 :sm 24 :md 24 :lg 24}
    component]])

(defn footer []
  (let [site-info (rf/subscribe [:site-info])]
    [:> Layout.Footer {:className "site-layout-footer"}
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

(def ^:dynamic *open-keys* (r/atom ["10"]))
(def ^:dynamic *selected-keys* (r/atom ["10"]))

(defn menu-click!
  [item]
  (let [item (js->clj item :keywordize-keys true)]
    (reset! *open-keys* (:keyPath item))
    (reset! *selected-keys* (:key item))))

(defn make-menu
  [menus]
  (doall
    (for [menu menus]
      (let [{:keys [id name url pid children]} menu]
        (if (empty? children)
          [:> Menu.Item {:key     id
                         :on-click #(navigate! (str "#" url))} name]
          [:> Menu.SubMenu {:key id :title name}
           (make-menu children)])))))

(defn menu []
  (let [user (rf/subscribe [:user])
        menus (:menus @user)
        menus-tree (utils/make-tree menus)]
    (fn []
      [:> Menu {:mode              "inline"
                :style             {:height "100%" :borderRight 0}
                :on-click          menu-click!
                :onOpenChange      (fn [item]
                                     (reset! *selected-keys* item)
                                     (reset! *open-keys* item))
                :open-keys         @*open-keys*
                :selected-keys     @*selected-keys*}

       (make-menu (:children menus-tree))])))

(defn manager-header-dropdown []
  (let [user (rf/subscribe [:user])]
    [:div {:style {:text-align "right"}}
     [:> antd/Dropdown {:overlay (r/as-element [manager-user-nav])}
      [:a {:className "ant-dropdown-link"
           :href      "#"}
       [:> UserOutlined]
       "  " (:name @user)]]]))

(defn manager-layout [main]
  [:> antd/Layout
   [header manager-header-dropdown]
   [:> antd/Layout
    [:> Layout.Sider {:className "site-layout-sidebar"}
     [menu]]
    [:> antd/Layout {:style {:padding "0 24px 24px"}}
     [manager-breadcrumb]
     [:> antd/Layout.Content {:className "site-layout-content"}
      main]]]
   [footer]])

(defn page-nav [handler]
  (let
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

(defn modal
  [modal-option content]
  [:> Modal
   (merge {:okText     "保存"
           :cancelText "退出"}
     modal-option)
   content])

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

