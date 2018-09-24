(ns soul-talk.pages.common
  (:import goog.history.Html5History)
  (:require [re-frame.core :refer [dispatch]]
            [reagent.core :as r]
            [cljsjs.showdown]))

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


(defn highlight-code [node]
  (let [nodes (.querySelectorAll (r/dom-node node) "pre code")]
    (loop [i (.-length nodes)]
      (when-not (neg? i)
        (when-let [item (.item nodes i)]
          (.highlightBlock js/hljs item))
        (recur (dec i))))))

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