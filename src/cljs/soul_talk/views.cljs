(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.home :refer [home-page]]
            [soul-talk.pages.dash :refer [dash-page]]))

;;多重方法  响应对应的页面
(defmulti pages (fn [page _] page))

(defmethod pages :home [_ _] [home-page])
(defmethod pages :dash [_ _] [dash-page])
(defmethod pages :default [_ _] [:div "default show ......"])

;; 根据配置加载不同页面
(defn main-page []
  (r/with-let [active-page (subscribe [:active-page])]
    (pages @active-page)))