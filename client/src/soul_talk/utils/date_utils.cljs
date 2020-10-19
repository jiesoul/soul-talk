(ns soul-talk.date-utils
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

