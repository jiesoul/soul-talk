(ns soul-talk.core
  (:require [soul-talk.dash :as dash]
            [reagent.core :as r]
            [soul-talk.home :as home]
            [domina :as dom]
            [taoensso.timbre :as log]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.history.Html5History))

(def app-state (r/atom {}))

(defn hook-browser-navigation! []
  (doto
    (Html5History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (secretary/defroute
    "/" []
    (swap! app-state assoc :page :home))

  (secretary/defroute
    "/dash" []
    (swap! app-state assoc :page :dash))

  (hook-browser-navigation!))


(defmulti current-page #(@app-state :page))
(defmethod current-page :home []
  [home/home-component])
(defmethod current-page :dash []
  [dash/dash-component])
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