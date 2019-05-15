(ns soul-talk.coeffects
  (:require [re-frame.core :refer [reg-cofx]]))

(reg-cofx
  :now
  (fn [cofx _]
    (assoc cofx :now (js/Date.))))
