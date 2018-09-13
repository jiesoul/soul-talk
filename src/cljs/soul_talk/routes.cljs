(ns soul-talk.routes
  (:require [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [secretary.core :as secretary]
            [accountant.core :as accountant]
            [re-frame.core :as rf])
  (:import goog.history.Html5History))

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))


(defn href
  [url]
  {:href (str js/context url)})

(defn navigate!
  [url]
  (accountant/navigate! url))

(defn run-events
  [events]
  (doseq [event events]
    (rf/dispatch event)))

;;------------
;; routes
(secretary/defroute "/" []
  (run-events [:home]))

;; History
(defn hook-browser-navigation! []
  (doto
    (Html5History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true))
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!))