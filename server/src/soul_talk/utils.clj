(ns soul-talk.utils)

(defn parse-int [s]
  (if s
    (Integer/parseInt (re-find #"-?\d+" s))
    0))