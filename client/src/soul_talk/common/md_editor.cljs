(ns soul-talk.common.md-editor
  (:require [reagent.dom :as rd]
            [reagent.core :as r]
            [re-frame.core :refer [dispatch subscribe]]))

(def ^{:private true} hint-limit 10)

(def ^{:private true} word-pattern-left #".*(#.*)$")

(def ^{:private true} word-pattern-right #"^(#?.*).*")

(defn- current-word [cm]
  (let [cursor (.getCursor cm "from")
        line (.-line cursor)
        char (.-ch cursor)
        text (.getLine (.-doc cm) line)
        parts (.split-at char text)
        leftp (->> (first parts) (apply str) (re-matches word-pattern-left) second)
        rightp (->> (second parts) (apply str) (re-matches word-pattern-right) second)]
    {:word (str leftp rightp)
     :from (- char (count leftp))
     :to (+ char (count rightp))}))

(defn- apply-hint [editor self data]
  (let [cursor (.getCusor editor "from")
        pos (current-word editor)
        from (:from pos)
        to (:to pos)
        word (:word pos)]
    (.replaceRange editor
      (str "[#" (.-text data) "](/issue/" (.-text data) ")")
      (clj->js {:line (.-line cursor)
                :ch from})
      (clj->js {:line (.-line cursor)
                :ch to}))))

(defn- render-hint [element self data]
  (rd/render
    [:div.list-group-item
     [:span "#" (get-in (js->clj data) ["displayText" "support-issue-id"])]
     " "
     [:span (get-in (js->clj data) ["displayText" "title"])]]
    element))

(defn- create-hint [hint]
  {:text (str (:support-issues-id hint))
   :displayName hint
   :render render-hint
   :hint apply-hint})

(defn- markdown-hints [hints]
  (fn [c o]
    (clj->js {:list (map create-hint hints)
              :from (.getCursor c "from")})))

(defn- show-hint [cm]
  (fn [k r os ns]
    (.showHint
      cm
      (clj->js {:hint (markdown-hints (:issues @ns))
                :completeSingle false}))))

(defn- sent-hint-request [cm]
  (let [current (:word (current-word cm))]
    (if (not (empty? current))
      (dispatch [:get-hints (subs current 1) hint-limit]))))

(defn- editor-set-shortcut [editor]
  (aset
    editor
    "options"
    "extraKeys"
    (clj->js
      {"Ctrl-Space" sent-hint-request})))

(extend-type js/NodeList
  IIndexed
  (-nth
    ([array n]
      (if (< n (alength array)) (aget array n)))
    ([array n not-found]
      (if (< n (alength array)) (aget array n)
                                not-found))))

(defn- inject-editor-implementation [editor]
  (do
    ;; move editor into text area
    (-> editor .-codemirror .toTextArea)
    ;; create new instance via fromTextArea
    (aset
      editor
      "codemirror"
      (.fromTextArea js/CodeMirror (-> editor .-codemirror .getTextArea)))
    ;; 操作DOM 元素到正确的地方
    (.insertBefore (-> editor
                     .-codemirror
                     .getScrollerElement
                     .-parentNode
                     .-parentNode)
                    (-> editor
                      .-codemirror
                      .getScrollElement
                      .-parentNode)
                    (-> editor
                      .-codemirror
                      .getScrollerElement
                      .-parentNode
                      .-parentNode
                      .-parentNode
                      .-childNodes
                      (nth 3)))))

(defn editor [text keys]
  (r/create-class
    {:display-name "me-editor"
     :component-did-mount
                   (fn [this olg-argv]
                     (let [editor      (js/SimpleMDE.
                                         (clj->js
                                           {:display-name    "md-editor"
                                            :status          true
                                            :placeholder     "正文"
                                            :toolbar         ["bold" "italic" "strikethrough" "|"
                                                              "heading" "code" "quote" "|"
                                                              "unordered-list" "ordered-list" "|"
                                                              "link" "image" "|"
                                                              "preview" "guide" "|"
                                                              {:name      "upload"
                                                               :action    (fn [] (.modal (js/$ "#uploadMdModal") "show"))
                                                               :className "fa fa-file"
                                                               :title     "upload md file"}]
                                            :renderingConfig {:codeSyntaxHighlighting true}
                                            :element         (rd/dom-node this)
                                            :force-sync      true
                                            :initialValue    @text
                                            :value @text}))
                           hints-shown (atom false)]
                       (do
                         (js/console.log (.value editor))
                         ;(inject-editor-implementation editor)
                         ;(editor-set-shortcut (-> editor .codemirror))
                         ;(add-watch hints :watch-hints (show-hint (-> editor .-codemirror)))
                         (-> editor
                           .-codemirror
                           (.on "change" #(let [value (.value editor)]
                                            (js/console.log value)
                                            (reset! text value))))
                         ;(-> editor .-codemirror (.on "change" (fn [] (when @hints-shown (sent-hint-request (-> editor .-codemirror))))))
                         ;(-> editor .-codemirror (.on "startCompletion" (fn [] (reset! hints-shown true))))
                         ;(-> editor .-codemirror (.on "endCompletion" (fn [] (reset! hints-shown false))))
                         )))
     :reagent-render
                   (fn [text keys]
                     (js/console.log "editor content: " @text)
                     [:textarea {:default-value @text
                                 :value @text}])}))

(defn editor-1 [text keys]
  [:div
   [:> js/SimpleMDE
    {:value @text
     :on-change #(dispatch [:update-value keys (.-value %)])}]])