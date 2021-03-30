(ns soul-talk.common.views
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [re-frame.core :as rf]
            ["react-highlight.js" :as hljs]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.utils :as utils]
            ["semantic-ui-react" :as sui :refer [Breadcrumb Container Menu Divider Dropdown Grid Segment Dimmer Loader
                                                 Image Message Button Pagination Header Visibility List]]
            [react :as react]))

(defn sleep [f ms]
  (js/setTimeout f ms))

(defn lading []
  (let [lading? (rf/subscribe [:loading?])]
    (when @lading?
      [:> Dimmer {:active @lading?
                  :inverted true}
       [:> Loader {:size "large"} "加载中"]])))

(defn logo []
  (let [site-info (rf/subscribe [:site-info])]
    [:div
     (:name @site-info)]))

(defn copyright []
  (let [year (.getFullYear (js/Date.))]
    [:> Container {:text-align "center"}
     "Copyright ©"
     [:a {:href "https://www.jiesoul.com/"}
      "jiesoul"]
     (str " 2019-" year ".")]))

(defn app-bar [& opts]
  (let [active-page (rf/subscribe [:active-page])]
    [:> Menu (merge {:secondary true
                     :pointing true} (first opts))
     [:> Container
      [:> Menu.Item {:as   "h2"
                     :name "jiesoul的个人网站"
                     :header true}]
      [:> Menu.Menu {:position "right"}
       [:> Menu.Item {:as     "a"
                      :name   "主页"
                      :active (if (= :home @active-page) true false)
                      :on-click #(navigate! "/")}]
       [:> Menu.Item {:as "a"
                      :name "文章"
                      :active (if (= :articles @active-page) true false)
                      :on-click #(navigate! "/articles")}]
       [:> Menu.Item {:as "a"
                      :name "系列"
                      :active (if (= :series @active-page) true false)
                      :on-click #(navigate! "/series")}]
       [:> Menu.Item {:as "a"
                      :name "标签"
                      :active (if (= :tags @active-page) true false)
                      :on-click #(navigate! "/tags")}]
       [:> Menu.Item {:as "a"
                      :name "关于"
                      :active (if (= :about @active-page) true false)
                      :on-click #(navigate! "/about")}]]]]))

(defn footer []
  [:> Segment {:inverted true}
   [:> Container {:text-align "center"}
    [:> Divider {:inverted true
                 :section  true}]
    [:> List {:horizontal true
              :inverted   true
              :divided    true
              :link       true
              :size       "small"}
     [:> List.Item {:as "a"} "text"]
     [:> List.Item {:as "a"} "text"]
     [:> List.Item {:as "a"} "text"]]]
   [copyright]])

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



(defn default-page []
  [:div "页面未找到，请检查链接！"])

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