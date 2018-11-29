(ns soul-talk.pages.admin
  (:require [reagent.core :as r]
            [baking-soda.core :as bs]
            [re-frame.core :refer [subscribe dispatch]]))

(defonce main-fields (r/atom nil))
(defonce table-data (r/atom []))

(defn table-component [data]
  (fn []
    [:div
     [:h2 "Section title"]
     [:div.table-responsive
      [:table.table.table-striped.table-sm
       [:thead
        [:tr
         [:th "#"]
         [:th "Header"]
         [:th "Header"]
         [:th "Header"]
         [:th "Header"]]]
       [:tbody
        (for [{:keys [title time author public] :as d} data]
          ^{:key d} [:tr
                     [:td title]
                     [:td time]
                     [:td author]
                     [:td public]])]]]]))

(defn foo []
  (r/create-class
    {:reagent-render (fn []
                       [:div "hello, world!"])}))

(defn foo1 []
  (let [component-state (r/atom {:count 0})]
    (fn []
      [:div
       [:p "Current count is: " (get @component-state :count)]
       [:button {:on-click #(swap! component-state update-in [:count] inc)}
        "inc"]])))

(defn foo-mistak []
  (let [component-state (r/atom {:count 0})]
    [:div
     [:p "Current count is: " (get @component-state :count)]
     [:button {:on-click #(swap! component-state update-in [:count] inc)}
      "Increment"]]))

(defn foo-mistak2 []
  (let [component-state (r/atom {:count 0})]
    [:div
     [:p "Current count is: " (get @component-state :count)]
     (js/console.log (str "Foo Mistake 2 is being rendered"))
     [:button {:on-click #(swap! component-state update-in [:count] inc)}
      "Increment"]]))

(defn foo-inner-let []
  (let [component-state (r/atom {:count 0})]
    (fn []
      (let [count (get @component-state :count)]
        [:div
         [:p "Current count is: " count]
         [:button {:on-click #(swap! component-state update-in [:count] inc)}
          "Increment"]]))))

(def app-state (r/atom {:foo {:bar "hello, world"
                                :baz {:quux "Woot"}}}))

(defn inside-app-state []
  [:div (str "Inside app-state: " @app-state)])

(def foo-cursor (r/cursor app-state [:foo]))

(defn inside-foo-cursor []
  [:div (str "Inside foo-cursor: " @foo-cursor)])

(def foobar-cursor (r/cursor app-state [:foo :bar]))

(defn inside-foobar-cursor []
  [:div (str "Inside foobar-cursor: " @foobar-cursor)])

(def foobaz-cursor (r/cursor app-state [:foo :baz]))

(defn inside-foobaz-cursor []
  [:div (str "Inside foobaz-cursor: " @foobaz-cursor)])

(def foobazquux-cursor (r/cursor app-state [:foo :baz :quux]))

(defn inside-foobazquux-cursor []
  [:div (str "Inside foobazquux-cursor: " @foobazquux-cursor)])

(defn clock
  []
  [:div.example-clock
   {:style {:color @(subscribe [:time-color])}}
   (-> @(subscribe [:time])
     .toTimeString
     (clojure.string/split " ")
     first)])

(defn color-input
  []
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @(subscribe [:time-color])        ;; subscribe
            :on-change #(dispatch [:time-color-change (-> % .-target .-value)])}]])

(defn message
  []
  [:div.example-clock
   @(subscribe [:error])])

(defn message-input
  []
  [:div.color-input
   "message : "
   [:input {:type "text"
            ;:value     ""    ;; subscribe
            :on-change #(dispatch [:set-error (-> % .-target .-value)])}]])

(defn ui
  []
  [:div
   [:h1 "Hello world, it is now"]
   [clock]
   [color-input]
   [message]
   [message-input]])

(defn greet-number [num]
  [:div (str "Hello #" num)])

(defn more-button [counter]
  [:div {:class "button-class"
         :on-click #(swap! counter inc)}]
  "more")

(defn parent []
  (let [counter (r/atom 1)]
    (fn parent-render []
      [:div
       [more-button counter]
       [greet-number @counter]])))

(defonce app-state (r/atom {:show-modal? false}))

(defn toggle! [ratom]
  (swap! ratom update :show-modal? not))

(defn modal-example [ratom opts]
  (let [{:keys [button-label class]} opts
        show-modals? (get @ratom :show-modal? false)]
    [:div
     [bs/Button {:color "dangger"
                 :on-click #(toggle! ratom)}
      button-label]

     [bs/Modal {:is-open show-modals?
                :toggle #(toggle! ratom)
                :class class}
      [bs/ModalHeader
       "Modal title"]

      [bs/ModalBody
       "Lorem ipsum dolor sit"]

      [bs/ModalFooter
       [bs/Button {:color "primary"
                   :on-click #(toggle! ratom)}
        "Do Someting"]
       [bs/Button {:color "secondary"
                   :on-click #(toggle! ratom)}
        "Cancel"]]]]))

(defn main-component []
  (fn []
    [:div
     [modal-example app-state {:button-label "Click Me"
                               :class "mymodal"}]
     [table-component @table-data]]))

(reset! table-data [{:title "title1"
                     :time "2018"
                     :author "soul"
                     :public "是"}])

(reset! main-fields
        [main-component])