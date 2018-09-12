(ns soul-talk.views
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [soul-talk.pages.home :refer [home-page]]
            [soul-talk.pages.dash :refer [dash-page]]))

(defmulti pages (fn [page _] page))

(defmethod pages :home [_ _] [home-page])
(defmethod pages :dash [_ _] [dash-page])

(defmethod pages :default [_ _] [:div])

(defn main-page []
  (r/with-let [active-page (subscribe [:active-page])
               user (subscribe [:user])]
     (if @user
       [:div
        [:div.container.content
         (pages @active-page @user)]]
       (pages :login nil))))
