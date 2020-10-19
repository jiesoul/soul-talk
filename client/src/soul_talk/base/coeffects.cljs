(ns soul-talk.base.coeffects
  (:require [re-frame.core :as rf]
            [soul-talk.base.local-storage :as local-store]))

(rf/reg-cofx
  :now
  (fn [cofx _]
    (assoc cofx :now (js/Date.))))

(rf/reg-cofx
  :local-store
  (fn [cofx local-store-key]
    (assoc-in cofx [:local-store local-store-key] (local-store/get-item local-store-key))))

