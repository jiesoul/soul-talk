(ns soul-talk.utils
  (:require [cljs-time.format :as cf :refer [parse unparse formatter formatters]]
            [cljs-time.coerce :as tc]))

(def custom-formatter-date-time (formatter "yyyy-MM-dd HH:mm:ss"))
(def custom-formatter-date (formatter "yyyy-MM-dd"))


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

