(ns soul-talk.core
  (:require [soul-talk.dash :as dash]
            [reagent.core :as r]
            [soul-talk.home :as home]
            [domina :as dom]
            [taoensso.timbre :as log]
            [secretary.core :as secretary]
            [soul-talk.components.common :as c])
  (:import goog.history.Html5History))

(def app-state (r/atom {}))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (secretary/defroute
    "/" []
    (swap! app-state assoc :page :home))

  (secretary/defroute
    "/about" []
    (swap! app-state assoc :page :about))

  (c/hook-browser-navigation!))


(defmulti current-page #(@app-state :page))

(defmethod current-page :home []
  [home/home-component])

(defmethod current-page :about []
  [:div->p "这是 about 页面"])

(defmethod current-page :default []
  [home/home-component])

(defn ^:export init []
  (if (and js/document
           (.-getElementById js/document))
    (do
      (app-routes)
      (r/render [current-page]
                (dom/by-id "app")))))

(defn ^:export dash []
  (dash/init))