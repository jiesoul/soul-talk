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
            ["@material-ui/lab" :refer [TreeView TreeItem Alert]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]
            [react :as react]))

(def validate-messages {:required "${label} 必须的"
                        :types {:email "${label} 非法邮件格式"
                                :url "${label} 非法地址"}})

(defn lading-backdrop [{:keys [classes]}]
  (let [lading? (rf/subscribe [:loading?])]
    (fn []
      (when @lading?
        [:> mui/Backdrop {:class-name (.-backdrop classes)
                          :open @lading?}
         [:> mui/CircularProgress {:color "inherit"}]]))))

(defn success-snackbars [{:keys [classes]}]
  (let [success (rf/subscribe [:success])]
    (when @success
      [:> mui/Snackbar {:anchor-origin {:vertical   "top"
                                        :horizontal "center"}
                        :class-name (.-root classes)
                        :open          true
                        :message @success
                        :key "success"
                        :auto-hide-Duration 3000
                        :on-close #(rf/dispatch [:clean-success])
                        :action (r/as-element [:> mui/IconButton {:size "small"
                                                                  :aria-label "close"
                                                                  :color "inherit"
                                                                  :on-click #(rf/dispatch [:clean-success])}
                                               [:> mui-icons/Close {:font-size "small"}]])}])))

(defn error-snackbars [{:keys [classes]}]
  (let [error (rf/subscribe [:error])]
    (when @error
      [:> mui/Snackbar {:anchor-origin {:vertical   "top"
                                        :horizontal "center"}
                        :class-name (.-root classes)
                        :open          true
                        :message @error
                        :key "error"
                        :auto-hide-Duration 3000
                        :on-close #(rf/dispatch [:clean-error])
                        :action (r/as-element [:> mui/IconButton {:size "small"
                                                                  :aria-label "close"
                                                                  :color "inherit"
                                                                  :on-click #(rf/dispatch [:clean-error])}
                                               [:> mui-icons/Close {:font-size "small"}]])}])))

(def ^:dynamic *drawer-open* (r/atom true))
(def ^:dynamic *anchor-el* (r/atom nil))

(defn handle-drawer-open
  []
  (reset! *drawer-open* true))
(defn handle-drawer-close
  []
  (reset! *drawer-open* false))

(def user-popover-menus [{:url "/users/profile" :text "个人信息" :icon mui-icons/Home}
                         {:url "/users/password" :text "修改密码" :icon mui-icons/Send}])

(defn user-popover [{:keys [classes]}]
  (let [user (rf/subscribe [:user])
        handle-popover-open (fn [event]
                              (reset! *anchor-el* (.-currentTarget event)))
        handle-popover-close (fn []
                               (reset! *anchor-el* nil))
        open (not (nil? @*anchor-el*))]
    (if @user
      [:div
       [:> mui/Button {:aria-controls "user-menu"
                       :aria-haspopup "true"
                       :color         "default"
                       :start-color (r/as-element [:> mui-icons/AccountCircle ])
                       :on-click      #(handle-popover-open %)}
        (:name @user)]
       [:> mui/Menu {:id                    "user-menu"
                     :class-name            (.-paper classes)
                     :elevation             0
                     :get-content-anchor-el nil
                     :anchor-el             @*anchor-el*
                     :keep-mounted          true
                     :open                  open
                     :on-close              handle-popover-close
                     :anchor-origin         {:vertical   "bottom"
                                             :horizontal "left"}
                     :transform-origin      {:vertical   "top"
                                             :horizontal "left"}}
        (doall
          (for [{:keys [url icon text]} user-popover-menus]
            [:> mui/MenuItem {:class-name (.-root classes)
                              :on-click   #(navigate! url)}
             [:> mui/ListItemIcon
              [:> icon {:font-size "small"}]]
             [:> mui/ListItemText {:primary text}]]))
        [:> mui/Divider]
        [:> mui/MenuItem {:class-name (.-root classes)
                          :on-click #(rf/dispatch [:logout])}
         [:> mui/ListItemIcon
          [:> mui-icons/ExitToApp {:font-size "small"}]]
         [:> mui/ListItemText {:primary "退出"}]]]])))

(defn styled-user-popover []
  (styles/with-custom-styles user-popover styles/popover-styles))

(defn app-bar [{:keys [classes] :as props}]
  (let [site-info (rf/subscribe [:site-info])
        app-bar-class (.-appBar classes)
        menu-button-class (.-menuButton classes)]
    [:> mui/AppBar {:position   "fixed"
                    :class-name app-bar-class}
     [:> mui/Toolbar
      [:> mui/Typography {:component  "h1"
                          :variant    "h6"
                          :no-wrap    true
                          :color "inherit"
                          :class-name (.-title classes)}
       (:name @site-info)]
      (styled-user-popover)
      ]]))

(defn menu-tree-items [{:keys [classes color bgColor] :as props} menus]
  (doall
    (for [menu menus]
      (let [{:keys [children id pid url]} menu]
        ^{:key menu}
        [:> TreeItem
         {:nodeId  (str id)
          :label   (r/as-element
                     (let []
                       [:div {:class-name (.-treeItemLabelRoot classes)}
                        [:> mui/Typography {:variant    "inherit"
                                            :class-name (.-treeItemLabelText classes)}
                         [:> mui/Button
                          (if (empty? children)
                            {:on-click #(do
                                          (rf/dispatch [:menus/select menu])
                                          (navigate! url))})
                          (:name menu)]]]))
          :style   {"--tree-view-color"    color
                    "--tree-view-bg-color" bgColor}
          :classes {:root     (.-treeItemRoot classes)
                    :content  (.-treeItemContent classes)
                    :expanded (.-treeItemExpanded classes)
                    :selected (.-treeItemSelected classes)
                    :group    (.-treeItemGroup classes)
                    :label    (.-treeItemLabel classes)}}
         (when-not (empty? children)
           (menu-tree-items props children))]))))

(defn menu-tree-view [{:keys [classes] :as props}]
  (let [user (rf/subscribe [:user])
        menus (:menus @user)
        menus-tree (utils/make-tree menus)
        {:keys [id pid]} @(rf/subscribe [:menus/selected])]
    (fn []
      [:> TreeView {:class-name            (.-treeRoot classes)
                    :default-selected      [(str id)]
                    :default-expanded      [(str pid)]
                    :default-collapse-icon (r/as-element [:> mui-icons/ArrowDropDown])
                    :default-expand-icon   (r/as-element [:> mui-icons/ArrowRight])
                    :default-end-icon      (r/as-element [:div {:style {:width 24}}])}
       (menu-tree-items props (:children menus-tree))]
      )))

(defn drawer [{:keys [classes] :as props}]
  (let [drawer-paper (.-drawerPaper classes)]
    [:> mui/Drawer
     {:variant "permanent"
      :class-name (.-drawer classes)
      :classes {:paper drawer-paper}}
     [:> mui/Toolbar]
     [:div
      {:class-name (.-drawerContainer classes)}

      [:> mui/Divider]
      [menu-tree-view props]]]))

(defn breadcrumbs [{:keys [classes]}]
  (let [{:keys [name pid]} @(rf/subscribe [:menus/selected])
        menus (:menus @(rf/subscribe [:user]))
        parent (first (filter #(= pid (:id %)) menus))]
    [:> mui/Breadcrumbs {:aria-label "breadcrumb"
                         :class-name (.-breadcrumb classes)}
     (if parent
       [:> mui/Typography {:color "inherit"} (:name parent)])
     [:> mui/Typography {:color "textPrimary"} name]]))

(defn copyright [{:keys [classes] :as props}]
  (let [year (.getFullYear (js/Date.))
        site-info (rf/subscribe [:site-info])]
    [:div
     [:> mui/Typography {:variant "body2"
                         :color   "textSecondary"
                         :align   "center"}
      "Copyright ©"
      [:> mui/Link {:color "inherit"
                    :href  "https://www.jiesoul.com/"}
       (:author @site-info)]
      (str " " year ".")]]))

(defn layout [{:keys [classes] :as props} & children]
  [:div {:class-name (.-root classes)}
   [:> mui/CssBaseline ]
   [app-bar props]
   [drawer props]
   [:main {:class-name (.-mainContainer classes)}
    [:> mui/Toolbar]
    [breadcrumbs props]
    [:> mui/Divider]
    [:div
     children]
    [:> mui/Box {:pt 4}
     [copyright]]]])

(defn styled-layout [& component]
  )

(defn dialog [{:keys [open title cancel-text ok-text on-close on-ok] :as opts} & children]
  [:> mui/Dialog {:aria-labelledby        "alert-dialog-title"
                  :aria-describedby       "alert-dialog-description"
                  :disable-backdrop-click true
                  :style                  {:min-width "200px"}
                  :open                   open
                  :key (str "dialog" (random-uuid))}
   [:> mui/DialogTitle {:id                 "alert-dialog-title"
                        :disable-typography true
                        :style              {:margin  0
                                             :padding "5px"}}
    [:> mui/Typography {:variant "h6"} title]
    [:> mui/IconButton {:aria-label "close"
                        :on-click   on-close
                        :style      {:position "absolute"
                                     :right    "5px"
                                     :top      "1px"}}
     [:> mui-icons/Close]]]
   [:> mui/DialogContent {:dividers true}
    children]
   [:> mui/DialogActions
    [:> mui/Button {:on-click on-close :color "default"} (if cancel-text cancel-text "取消")]
    [:> mui/Button {:on-click on-ok :color "primary"} (if ok-text ok-text "保存")]]])

(defn default-page [props]
  [layout props
   [:div "页面未找到，请检查链接！"]])

(defn default []
  (styles/main default-page))

(defn table-page [event {:keys [total per_page page] :as params}]
  [:> mui/TablePagination {:rows-per-page-options   [10, 15, 100]
                           :component               "div"
                           :color                   "primary"
                           :variant                 "outlined"
                           :count                   total
                           :rows-per-page           per_page
                           :page                    (dec page)
                           :on-change-page          (fn [_ page]
                                                      (rf/dispatch [event
                                                                    (assoc params :page (inc page))]))
                           :on-change-rows-per-page (fn [e]
                                                      (let [value (-> e .-target .-value)]
                                                        (rf/dispatch [event
                                                                      (assoc params :per_page value)])))}])

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
                      :on-click #(navigate! "/users/profile")}
        "个人信息"]
       [:> Menu.Item {:key      "change-pass"
                      :icon     (r/as-element [:> EditOutlined])
                      :on-click #(navigate! "/users/password")}

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