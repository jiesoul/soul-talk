(ns soul-talk.common.views
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            ["react-highlight.js" :as hljs]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.utils :as utils]
            ["semantic-ui-react" :as sui :refer [Breadcrumb Container Menu Divider Dropdown Grid Segment Dimmer Loader
                                                 Sidebar Message Modal Button Pagination Header Visibility Confirm
                                                 TransitionablePortal Icon]]
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

(defn success-portal []
  (let [success (rf/subscribe [:success])]
    [:> TransitionablePortal {:open       (if @success true false)
                              :transition {:animation "fly up"
                                           :duration  100}
                              :on-close #(rf/dispatch [:clean-success])}
     [:> Message {:style {:left     "50%"
                          :position "fixed"
                          :top      "5%"
                          :z-index  1000}
                  :success true
                  :content @success}]]))

(defn error-portal []
  (let [error (rf/subscribe [:error])]
    [:> TransitionablePortal {:open       (if @error true false)
                              :transition {:animation "fly up"
                                           :duration  100}
                              :on-close #(rf/dispatch [:clean-error])}
     [:> Message {:style    {:left     "40%"
                             :position "fixed"
                             :top      "5%"
                             :z-index  1000}
                  :error true
                  :content @error}]]))


(defn info-portal []
  (let [info (rf/subscribe [:info])]
    [:> TransitionablePortal {:open       (if @info true false)
                              :transition {:animation "fly up"
                                           :duration  100}}
     [:> Message {:style    {:left     "40%"
                             :position "fixed"
                             :top      "5%"
                             :z-index  1000}
                  :info true
                  :content @info}]]))

(defn modal [header content action]
  (let [open @(rf/subscribe [:modal])]
    [:> Modal {:open open}
     [:> Modal.Header header]
     [:> Modal.Content {:image true
                        :scrolling true}
      content]
     [:> Modal.Action
      action]]))

(defn confirm []
  (let [{:keys [open title cancel-text ok-text on-confirm content]} @(rf/subscribe [:confirm])]
    (when open
      [:> Confirm {:open           open
                   :key            (str "confirm" (random-uuid))
                   :confirm-button (if ok-text ok-text "dqy")
                   :cancel-button  (if cancel-text cancel-text "取消")
                   :on-cancel      (rf/dispatch [:close-confirm])
                   :on-confirm     (do (on-confirm)
                                       (rf/dispatch [:close-confirm]))
                   :header         title
                   :content        content}])))

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
                           :on-click #(navigate! (str "/user/" (:id @user) "/password"))}
         "修改密码"]
        [:> Dropdown.Item {:as       "a"
                           :on-click #(navigate! (str "/user/" (:id @user) "/profile"))}
         "个人信息"]
        [:> Divider]
        [:> Dropdown.Item {:as       "a"
                           :on-click #(rf/dispatch [:logout])}
         "退出"]]]]]))

(defn menu-tree-items [menus selected-menu]
  (doall
    (for [menu menus]
      (let [{:keys [children id pid url name]} menu]
        ^{:key menu}
        (if (empty? children)
          [:> Menu.Item {:name     name
                         :active   (= id (:id selected-menu))
                         :on-click #(do
                                      (rf/dispatch [:menu/select menu])
                                      (navigate! url))}
           name]
          [:<>
           [:> Divider {:style {:margin "0"}}]
           [:> Menu.Item {:key name}
            [:> Menu.Header {:as "h5"} name]
            [:> Menu.Menu {:style {:padding-left "10px"}}
             (menu-tree-items children selected-menu)]]])))))

(defn sidebar []
  (let [user (rf/subscribe [:user])
        menus (:menus @user)
        menus-tree (utils/make-tree menus)
        select-menu @(rf/subscribe [:menu/selected])]
    [:> Menu {:vertical true
              :fluid true
              :borderless true
              :pointing true}
     (menu-tree-items (:children menus-tree) select-menu)]))

(defn out-breadcrumb [data]
  (let [c (count data)
        b (first data)]
    (if (<= c 1)
      [:> Breadcrumb.Section {:active true} b]
      [:<>
       [:> Breadcrumb.Section b]
       [:> Breadcrumb.Divider "/"]
       (out-breadcrumb (rest data))])))

(defn breadcrumbs []
  (let [breadcrumbs-data @(rf/subscribe [:breadcrumb])
        c (count breadcrumbs-data)]
    [:> Breadcrumb
     (out-breadcrumb breadcrumbs-data)]))

(defn copyright []
  (let [year (.getFullYear (js/Date.))
        site-info (rf/subscribe [:site-info])]
    (if @site-info
      [:> Container {:text-align   "center"}
       "Copyright ©"
       [:a {:href     "https://www.jiesoul.com/"}
        "jiesoul"]
       (str " 2019-" year ".")])))

(defn layout [children]
  [:> Grid {:doubling true}
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

(defn form-layout [children]
  [:> Grid {:centered true
            :columns 2}
   [:> Grid.Column
    children]])



(defn default-page []
  [layout
   [:div "页面未找到，请检查链接！"]])

(defn default []
  [default-page])

(defn table-page [event {:keys [total per_page page total_pages] :as params}]
  [:div {:style {:text-align "center"}}
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
                                       (rf/dispatch [event (assoc params :page active-page)])))}]])

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