(ns soul-talk.pages.admin
  (:require [reagent.core :as r]
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

(defn main-component []
  (fn []
    [:div
     [table-component @table-data]]))

(reset! table-data [{:title "title1"
                     :time "2018"
                     :author "soul"
                     :public "是"}])

(reset! main-fields
        [main-component])