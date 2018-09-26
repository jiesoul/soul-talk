(ns soul-talk.pages.common
  (:import goog.history.Html5History)
  (:require [re-frame.core :refer [dispatch]]
            [reagent.core :as r]
            [cljsjs.showdown]
            [cljsjs.highlight]
            [cljsjs.simplemde]
            [taoensso.timbre :as log]))

(defn input [type id placeholder fields]
  "标准 input， 其中的 on-change 实现了值的绑定"
  [:input.form-control.input-lg
   {:type type
    :placeholder placeholder
    :value (id @fields)
    :on-change #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label]
   (if optional?
     [input type id placeholder fields]
     [:div.input-group
      [input type id placeholder fields]
      [:span.input-group-addon.text-danger "*"]])])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))


(defn modal [id header body footer]
  [:div {:id id}
   [:div.modal-dialog {:role "document"}
    [:div.modal-content
     [:div.modal-header
      [:h3.mb-3.font-weight-normal.text-center header]
      [:button.close
       {:data-dismiss "modal"
        :aria-label "Close"
        :type :button}
       [:i.fa.fa-times {:aria-hidden "true"}]]]
     [:div.modal-body body]
     [:div.modal-footer footer]]]])

(defn editor [text]
  (r/create-class
    {:component-did-mount
     #(let [editor (js/SimpleMDE.
                     (clj->js
                       {:auto-focus true
                        :spell-check false
                        :placeholder "正文"
                        :toolbar ["bold"
                                  "italic"
                                  "strikethrough"
                                  "|"
                                  "code"
                                  "quote"
                                  "|"
                                  "unordered-list"
                                  "ordered-list"
                                  "|"
                                  "link"]
                        :renderingConfig {:codeSyntaxHighlighting true}
                        :element (r/dom-node %)
                        :initialValue @text}))]
        (-> editor .-codemirror (.on "change" (fn [] (reset! text (.value editor))))))
     :reagent-render
     (fn [] [:textarea])}))

;;高亮代码 循环查找结节
(defn highlight-code [node]
  (let [nodes (.querySelectorAll (r/dom-node node) "pre")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock js/hljs item))
        (recur (dec i))))))

;; 处理 markdown 转换
(defn markdown-preview []
  (let [md-parser (js/showdown.Converter.)]
    (r/create-class
      {:component-did-mount
       #(highlight-code (r/dom-node %))
       :component-did-update
       #(highlight-code (r/dom-node %))
       :reagent-render
       (fn [content]
         [:div
          {:dangerouslySetInnerHTML
           {:__html (.makeHtml md-parser (str content))}}])})))