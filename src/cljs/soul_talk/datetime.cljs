(ns soul-talk.datetime
  (:require [cljs-time.format :as f]
            [cljs-time.coerce :as c]))

(defn format-date [date]
  (f/unparse (f/formatters "yyyy-MM-dd")
             (c/from-date date)))

(defn format-date-time [date]
  (f/unparse (f/formatters "yyyy-MM-dd HH:mm:ss")
             (c/from-date date)))
