(ns soul-talk.common.common
  (:require [re-frame.core :as rf :refer [dispatch subscribe]]
            [reagent.core :as r]
            [reagent.dom :as rd]
            [showdown]
            ["react-highlight.js" :as hljs]
            [antd :as antd]))

(defn to-time [date]
  (str (.toDateString (js/Date. date))))

(defn loading-modal []
  (r/with-let [loading? (subscribe [:loading?])]
    (fn []
      (when @loading?
        [:> antd/Spin {:tip  "加载中。。。。"
                       :size "large"}]))))

(defn spin-loading []
  (r/with-let [loading? (subscribe [:loading?])]
    (when @loading?
      (antd/message.loading "正在加载中。。。。"))))

(defn success-modal []
  (r/with-let [success (subscribe [:success])]
    (when @success
      (antd/message.success @success)
      (dispatch [:clean-success]))))

(defn show-confirm
  [title content ok-fun cancel-fun]
  (antd/Modal.confirm
    (clj->js {:centered true
              :title    title
              :content  content
              :onOk     ok-fun
              :onCancel cancel-fun})))

(defn error-modal []
  (r/with-let [error (subscribe [:error])]
    (when @error
      ;(antd/message.error @error)
      (dispatch [:clean-error]))))


(defn form-modal [title content state success-fn cancel-fn]
  [:> antd/Modal
   {:title    title
    :visible  state
    :onOk     success-fn
    :onCancel cancel-fn}
   content])

(defn breadcrumb-component []
  (r/with-let [items (subscribe [:breadcrumb])]
    (fn []
      [:> antd/Breadcrumb
       (for [item @items]
         ^{:key item}
         [:> antd/Breadcrumb.Item item])])))

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

(defn page-nav [handler]
  (r/with-let
    [pagination (subscribe [:admin/pagination])
     prev-page (r/cursor pagination [:previous])
     next-page (r/cursor pagination [:next])
     page (r/cursor pagination [:page])
     pre-page (r/cursor pagination [:pre-page])
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
            {:on-click  #(dispatch [handler (assoc paginate-params :page @prev-page)])
             :tab-index "-1"}
            "Previous"]]
          (doall
            (for [p (range start end)]
              ^{:key p}
              [:li.page-item
               {:class (if (= p @page) "active")}
               [:a.page-link
                {:on-click #(dispatch [handler (assoc paginate-params :page p)])}
                p]]
              ))
          [:li.page-item
           {:class (if (> @next-page @total-pages) "disabled")}
           [:a.page-link
            {:on-click #(dispatch [handler (assoc paginate-params :page @next-page)])}
            "Next"]]]]))))

