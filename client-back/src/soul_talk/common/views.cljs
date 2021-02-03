(ns soul-talk.common.views
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            ["react-highlight.js" :as hljs]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.utils :as utils]
            [soul-talk.common.styles :as styles]
            ["semantic-ui-react" :as sui :refer [Breadcrumb Container Menu Divider Dropdown Grid Segment Dimmer Loader
                                                 Sidebar Message Modal Button Pagination Header Visibility]]
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
      [:> Dimmer {:active @lading?
                  :inverted true}
       [:> Loader {:size "large"} "加载中"]])))

(defn progress []
  )

(defn success []
  (let [success (rf/subscribe [:success])
        on-close #(rf/dispatch [:clean-success])]
    (when @success
      (toast.success @success {:position    (-> toast .-POSITION .-TOP_CENTER)
                               :toast-id "success-toast"
                               :auto-close       3000
                               :on-close    on-close})
      (on-close))))

(defn error []
  (let [error (rf/subscribe [:error])
        on-close #(rf/dispatch [:clean-error])]
    (when @error
      (toast.error @success {:position    (-> toast .-POSITION .-TOP_CENTER)
                             :toast-id "error-toast"
                             :auto-close       3000
                             :on-close    on-close})
      (on-close))))

(defn app-bar []
  (let [site-info (rf/subscribe [:site-info])
        user (rf/subscribe [:user])]
    [:> Menu {:inverted true}
     [:> Container {:fluid true}
      [:> Menu.Item {:as     "h3"
                     :header true}
       (:name @site-info)]
      [:> Dropdown {:item       true
                    :simple     true
                    :pointing   "right"
                    :icon       "user"
                    :class-name "icon"
                    :style      {:margin-right "10px"}
                    :text       (:name @user)}
       [:> Dropdown.Menu
        [:> Dropdown.Item {:as       "a"
                           :on-click #(navigate! (str "/users/" (:id @user) "/password"))}
         "修改密码"]
        [:> Dropdown.Item {:as       "a"
                           :on-click #(navigate! (str "/users/" (:id @user) "/profile"))}
         "个人信息"]
        [:> Divider]
        [:> Dropdown.Item {:as       "a"
                           :on-click #(rf/dispatch [:logout])}
         "退出"]]]]]))

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
            name
            [:> Menu.Menu
             (menu-tree-items children selected-id)]])]))))

(defn sidebar []
  (let [user (rf/subscribe [:user])
        menus (:menus @user)
        menus-tree (utils/make-tree menus)
        {:keys [id]} @(rf/subscribe [:menus/selected])]
    [:> Menu {:vertical true
              :fluid true}
     (menu-tree-items (:children menus-tree) id)]))

(defn breadcrumbs []
  (let [[first second] @(rf/subscribe [:breadcrumb])]
    [:> Breadcrumb
     (if second
       [:<>
        [:> Breadcrumb.Section first]
        [:> Breadcrumb.Divider "/"]
        [:> Breadcrumb.Section {:active true} second]]
       [:> Breadcrumb.Section {:active true} first])]))

(defn copyright []
  (let [year (.getFullYear (js/Date.))
        site-info (rf/subscribe [:site-info])]
    (if @site-info
      [:> Container {:text-align   "center"}
       "Copyright ©"
       [:a {:href     "https://www.jiesoul.com/"}
        (:author @site-info)]
       (str " 2019-" year ".")])))

(defn layout [children]
  [:> Grid
   [:> Grid.Row
    [:> Grid.Column {:width 16}
     [app-bar]]]
   [:> Grid.Row {:style {:margin "-1em 1em"}}
    [:> Grid.Column {:width 2}
     [sidebar]]
    [:> Divider {:vertical true}]
    [:> Grid.Column {:width 14}
     [:> Segment
      [breadcrumbs]
      [:> Divider]
      [:> Container {:max-width "lg"
                     :fluid true}
       children
       [:div {:style {:margin-top "20px"}}
        [copyright]]]]]]])

(defn modal [{:keys [open title cancel-text ok-text on-close on-ok] :as opts} & children]
  [:> Modal {:dimmer   "blurring"
                :on-close on-close
                :open     open
                :key      (str "modal" (random-uuid))
                :size     "mini"}
   [:> Modal.Header {:id                 "alert-dialog-title"
                     :disable-typography true
                     :style              {:margin  0
                                          :padding "5px"}}
    title]
   [:> Modal.Content children]
   [:> Modal.Actions
    [:> Button {:on-click on-close :negative true} (if cancel-text cancel-text "取消")]
    [:> Button {:on-click on-ok :positive true} (if ok-text ok-text "保存")]]])

(defn default-page []
  [layout
   [:div "页面未找到，请检查链接！"]])

(defn default []
  [default-page])

(defn table-page [event {:keys [total per_page page total_pages] :as params}]
  [:> Pagination {:active-page    page
                  :total-pages    total_pages
                  :first-item     {:aria-label "首页"
                                   :content    "首页"}
                  :last-item      {:aria-label "末页"
                                   :content    "末页"}
                  :prev-item      {:aria-label "上一页"
                                   :content    "上一页"}
                  :next-item      {:aria-label "下一页"
                                   :content    "下一页"}
                  :on-page-change (fn [e page]
                                    (let [active-page (.-activePage page)]
                                      (rf/dispatch [event (assoc params :page active-page)]))
                                    )}])

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