(ns soul-talk.pages.common
  (:require-macros)
  (:require [re-frame.core :refer [dispatch subscribe]]
            [reagent.core :as r]
            [baking-soda.core :as bs]
            [reagent-modals.modals :as rm]
            [cljsjs.showdown]))

(defn loading-throber []
  (let [loading? (subscribe [:loading?])]
    (when @loading?
      [bs/Modal {:is-open true}
       [bs/ModalBody
        [:div.spinner
         [:div.bounce1]
         [:div.bounce2]
         [:div.bounce3]]]])))

(defn success-modal []
  (when-let [success @(subscribe [:success])]
    [bs/Modal {:is-open (boolean success)}
     [bs/ModalBody
      [:p success]]
     [bs/ModalFooter
      [bs/Button {:color "primary"
                  :on-click #(dispatch [:set-success nil])}
       "Ok"]]]))

(defn error-modal []
  (when-let [error @(subscribe [:error])]
    [bs/Modal {:is-open (boolean error)}
     [bs/ModalHeader "An error has occured"]
     [bs/ModalBody
      [:p error]]
     [bs/ModalFooter
      [bs/Button {:color    "primary"
                  :on-click #(dispatch [:set-error nil])}
       "Ok"]]]))

(defn validation-modal [title errors]
  [bs/Modal {:is-open (boolean @errors)}
   [bs/ModalHeader title]
   [bs/ModalBody
    [:ul
     (doall
       (for [[_ error] @errors]
         ^{:key error}
         [:li error]))]]
   [bs/ModalFooter
    [:button.btn.btn-sm.btn-danger
     {:on-click #(reset! errors nil)}
     "Close"]]])

(defn confirm-modal
  [title confirm-open? action action-label]
  [bs/Modal {:is-open @confirm-open?}
   [bs/ModalHeader title]
   [bs/ModalFooter
    [:div.btn-toolbar
     [:button.btn.btn-sm.btn-danger
      {:on-click #(reset! confirm-open? false)}
      "Cancel"]
     [:button.btn.btn-sm.btn-success
      {:on-click #(do
                    (reset! confirm-open? false)
                    (action))}
      action-label]]]])

(defn input [type id placeholder fields]
  (fn []
    [:input.form-control.input-lg
     {:type        type
      :placeholder placeholder
      :value       (id @fields)
      :on-change   #(swap! fields assoc id (-> % .-target .-value))}]))

(defn form-input [type label id placeholder fields optional?]
  (fn []
    [:div.form-group
     [:label label]
     (if optional?
       [input type id placeholder fields]
       [:div.input-group
        [input type id placeholder fields]
        [:span.input-group-addon.text-danger "*"]])]))

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))


(defn modal [id header body footer]
  (fn []
    [:div {:id id}
     [:div.modal-dialog {:role "document"}
      [:div.modal-content
       [:div.modal-header
        [:h3.mb-3.font-weight-normal.text-center header]
        [:button.close
         {:data-dismiss "modal"
          :aria-label   "Close"
          :type         :button}
         [:i.fa.fa-times {:aria-hidden "true"}]]]
       [:div.modal-body body]
       [:div.modal-footer footer]]]]))

(defn upload-md-modal []
  (fn []
    [:div.modal.fade
     {:id "uploadMdModal"
      :tab-index -1
      :role "dialog"
      :aria-labelledby "uploadMdModalLabel"
      :aria-hidden true}
     [:div.modal-dialog
      {:role "document"}
      [:div.modal-content
       [:div.modal-header.text-center
        [:h5#uploadMdModalLabel.modal-title "导入"]
        [:button.close
         {:aria-label "Close"
          :data-dismiss "modal"}
         [:i.fa.fa-times]]]
       [:div.modal-content
        [:div.card
         [:div.card-body
          [:div.custom-file
           [:input#customFile.custom-file-input
            {:type      :file
             :on-change #(let [file (-> % .-target .-files (aget 0))]
                           (dispatch [:posts/upload file])
                           (.modal (js/$ "#uploadMdModal") "hide"))}]
           [:label.custom-file-label
            {:for "customFile"}
            "选择文件"]]]]]
       [:div.modal-footer]]]]))

(defn editor [text]
  (r/create-class
    {:display-name "md-editor"
     :component-did-mount
     #(let [editor (js/SimpleMDE.
                     (clj->js
                       {:display-name    "md-editor"
                        :auto-focus      true
                        :spell-check     false
                        :status          true
                        :placeholder     "正文"
                        :toolbar         ["bold"
                                          "italic"
                                          "strikethrough"
                                          "|"
                                          "heading"
                                          "code"
                                          "quote"
                                          "|"
                                          "unordered-list"
                                          "ordered-list"
                                          "|"
                                          "link"
                                          "image"
                                          "|"
                                          "side-by-side"
                                          "preview"
                                          "fullscreen"
                                          "guide"
                                          "|"
                                          {:name      "upload"
                                           :action    (fn [] (.modal (js/$ "#uploadMdModal") "show"))
                                           :className "fa fa-file"
                                           :title     "upload md file"}]
                        :renderingConfig {:codeSyntaxHighlighting true}
                        :element         (r/dom-node %)
                        :force-sync      true
                        :initialValue    @text
                        :value @text}))]
        (-> editor .-codemirror (.on "change" (fn [] (reset! text (.value editor))))))
     :reagent-render
     (fn [text] [:textarea#editMdTextarea
             {:default-value @text}])}))

;;高亮代码 循环查找结节
(defn highlight-code [node]
  (let [nodes (.querySelectorAll (r/dom-node node) "pre code")]
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