(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.routes :refer [navigate!]]
            [soul-talk.common.views :as c]
            [soul-talk.pages :as page]
            [soul-talk.article.views :as article]
            [soul-talk.tag.views :as tag]
            [soul-talk.series.views :as series]
            [soul-talk.utils :as utils]))

;;多重方法  响应对应的页面
(defmulti pages (fn [page _] page))

;;
(defmethod pages :home [_ _]
  [page/home])

;; article
(defmethod pages :articles [_ _]
  [page/articles])

(defmethod pages :articles/view [_ _]
  [article/view])

(defmethod pages :series [_ _]
  [page/series])

(defmethod pages :tags [_ _]
  [page/tags])

(defmethod pages :about [_ _]
  [page/about])


;; default
(defmethod pages :default [_ _] [page/home])

;; 根据配置加载不同页面
(defn main-page []
  (let [ready? (subscribe [:initialised?])
        active-page (subscribe [:active-page])]
    (when @ready?
      [:<>
       [c/lading]
       (pages @active-page)])))