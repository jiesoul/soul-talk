(ns soul-talk.utils
  (:require [cljs-time.format :as cf :refer [parse unparse formatter formatters]]
            [cljs-time.coerce :as tc]
            [reagent.core :as r]
            [re-frame.core :refer [subscribe]]))

(def custom-formatter-date-time (formatter "yyyy-MM-dd HH:mm:ss"))
(def custom-formatter-date (formatter "yyyy-MM-dd"))

(defn data->options [data key text value]
  (->> data
    (map #(assoc % :value (get % value)
                   :key (get % key)
                   :text (get % text)))
    (map #(select-keys % [:key :value :text]))))

(defn get-data-dic-by-id [id]
  (let [data-dices @(subscribe [:data-dices])]
    (first (filter #(= id (:id %)) data-dices))))

(defn to-date-time
  ([date]
   (to-date-time date custom-formatter-date-time))
  ([data formatter]
   (unparse formatter (tc/from-date data))))

(defn to-date
  ([date]
   (to-date date custom-formatter-date))
  ([date formatter]
   (unparse formatter (tc/from-date date))))

(defn to-time [date]
  (str (.toDateString (js/Date. date))))

(defn coll-to-in-str [coll]
  (subs
    (reduce #(str %1 "," (str "'" %2 "'")) "" coll) 1))

(defn make-tree
  ([coll] (let [root {:id 0 :name "root"}]
            (assoc root :children (make-tree root coll))))
  ([root coll]
   (for [x coll :when (= (:pid x) (:id root))]
     (assoc x :children (make-tree x coll)))))

(defn event-value
  [^js/Event e]
  (let [^js/HTMLInputElement el (.-target e)]
    (.-value el)))

(defn catch []
  (defn error-boundary [comp]
    (r/create-class
      {:constructor (fn [this props]
                      (set! (.-state this) #js {:error nil}))
       :component-did-catch (fn [this e info])
       :get-derived-state-from-error (fn [error] #js {:error error})
       :render (fn [this]
                 (r/as-element
                   (if-let [error (.. this -state -error)]
                     [:div
                      "发生错误"
                      [:button {:on-click #(.state this #js {:error nil})}
                       "重试"]]
                     comp)))})))

