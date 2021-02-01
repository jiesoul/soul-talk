(ns soul-talk.common.views
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            ["react-highlight.js" :as hljs]
            ["@material-ui/core" :as mui]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]
            ["@material-ui/lab" :refer [TreeView TreeItem Alert]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]
            ["semantic-ui-react" :as sui :refer [Breadcrumb Container Menu Divider Dropdown Grid Segment Dimmer Loader
                                                 Sidebar Message]]
            ["react-toastify" :refer [toast]]
            [react :as react]))

(def validate-messages {:required "${label} 必须的"
                        :types {:email "${label} 非法邮件格式"
                                :url "${label} 非法地址"}})

(defn sleep [f ms]
  (js/setTimeout f ms))

(defn lading []
  (let [lading? (rf/subscribe [:loading?])]
    (when @lading?
      [:> Dimmer {:active @lading?}
       [:> Loader {:size "large"} "加载中"]])))

(defn success []
  (let [success (rf/subscribe [:success])
        on-close #(rf/dispatch [:clean-success])]
    (when @success
      (toast.success @success {:position    (-> toast .-POSITION .-TOP_CENTER)
                                :auto-close       3000
                                :on-close    on-close})
      (rf/dispatch [:clean-success]))))

(defn error-snackbars [{:keys [classes]}]
  (let [error (rf/subscribe [:error])
        on-close #(rf/dispatch [:clean-error])]
    (when @error
      [:> mui/Snackbar {:anchor-origin {:vertical   "top"
                                        :horizontal "center"}
                        :class-name (.-root classes)
                        :open          true
                        :key "error"
                        :auto-hide-Duration 3000
                        :on-close on-close
                        :action (r/as-element [:> mui/IconButton {:size "small"
                                                                  :aria-label "close"
                                                                  :color "inherit"
                                                                  :on-click on-close}
                                               [:> mui-icons/Close {:font-size "small"}]])}
       [:> Alert {:on-close on-close
                  :severity "error"}
        @error]])))

(defn app-bar []
  (let [site-info (rf/subscribe [:site-info])
        user (rf/subscribe [:user])]
    [:> Menu {:inverted true
              :style {:padding "0"
                      :margin "0px"}}
     [:> Menu.Item {:as     "h3"
                    :header true}
      (:name @site-info)]
     [:> Dropdown {:item     true
                   :simple   true
                   :pointing "right"
                   :icon "user"
                   :class-name "icon"
                   :style {:margin-right "10px"}
                   :text     (:name @user)}
      [:> Dropdown.Menu
       [:> Dropdown.Item {:as "a"
                          :on-click #(navigate! (str "/users/" (:id @user) "/password"))}
        "修改密码"]
       [:> Dropdown.Item {:as "a"
                          :on-click #(navigate! (str "/users/" (:id @user) "/profile"))}
        "个人信息"]
       [:> Divider]
       [:> Dropdown.Item {:as "a"
                          :on-click #(rf/dispatch [:logout])}
        "退出"]]]]))

(defn menu-tree-items [menus selected-id]
  (doall
    (for [menu menus]
      (let [{:keys [children id pid url name]} menu]
        ^{:key menu}
        [:div
         (if (empty? children)
           [:> Menu.Item {:name     name
                          :active (= id selected-id)
                          :on-click #(do
                                       (rf/dispatch [:menus/select menu])
                                       (navigate! url))}]
           [:> Menu.Item
            [:> Menu.Header name]
            [:> Menu.Menu
             (menu-tree-items children selected-id)]])]))))

(defn drawer []
  (let [user (rf/subscribe [:user])
        menus (:menus @user)
        menus-tree (utils/make-tree menus)
        {:keys [id]} @(rf/subscribe [:menus/selected])]
    [:> Menu {:vertical true
              :inverted true}
     (menu-tree-items (:children menus-tree) id)]))

(defn breadcrumbs []
  (let [[first second] @(rf/subscribe [:breadcrumb])]
    [:> Breadcrumb {:size "large"}
     (if second
       [:<>
        [:> Breadcrumb.Section first]
        [:> Breadcrumb.Divider "/"]
        [:> Breadcrumb.Section {:active true} second]]
       [:> Breadcrumb.Section {:active true} first])]))

(defn copyright []
  (let [year (.getFullYear (js/Date.))
        site-info (rf/subscribe [:site-info])
        name (:name @site-info)]
    (if @site-info
      [:> mui/Typography {:variant "body2"
                          :color   "textSecondary"
                          :align   "center"}
       "Copyright ©"
       [:> mui/Link {:color    "inherit"
                     :href     "https://www.jiesoul.com/"
                     :children (:name @site-info)}
        (:name @site-info)]
       (str " " year ".")])))

(defn layout [children]
  [:div
   [app-bar]
   [:> Grid {:style {:padding "1px 20px 0px 0px"}}
    [:> Grid.Column {:width 2
                     :container true}
     [drawer]]
    [:> Grid.Column {:width 14
                     :container true}
     [:> Segment
      [:<>
       [breadcrumbs]
       [:> Divider]
       [:div {:max-width "lg"}
        children
        [:div {:style {:margin-top "20px"}}
         [copyright]]]]]]]])

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
    [:> mui/Button {:on-click on-close :color "secondary"} (if cancel-text cancel-text "取消")]
    [:> mui/Button {:on-click on-ok :color "primary"} (if ok-text ok-text "保存")]]])

(defn default-page [props]
  [layout props
   [:div "页面未找到，请检查链接！"]])

(defn default []
  (styles/styled-layout default-page))

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