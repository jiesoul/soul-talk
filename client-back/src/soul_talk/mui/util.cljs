(ns soul-talk.mui.util
  (:require [reagent.core :as r]
            [reagent.impl.util :refer [fun-name]]
            [clojure.walk :refer [postwalk]]))

(defn adapt-react-class
  ([c] (adapt-react-class c (fun-name c)))
  ([c display-name]
   (let [adapted (r/adapt-react-class c)]
     (set! (.-displayName adapted) display-name)
     adapted)))

(defn wrap-clj-function [f]
  (fn [& args]
    (js->clj '(apply f (map clj->js args)))))

(defn wrap-all-clj-functions [m]
  (postwalk (fn [x]
              (if (fn? x)
                (wrap-clj-function x)
                x))
    m))

(defn wrap-jss-styles [styles]
  (if (fn? styles)
    (fn [theme]
      (-> theme
        (styles)
        ))))
