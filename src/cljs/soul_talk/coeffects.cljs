(ns soul-talk.coeffects
  (:require [re-frame.core :refer [reg-cofx]]))

(reg-cofx
  :now
  (fn [coffects _]
    (assoc coffects :now (js/Date.))))
