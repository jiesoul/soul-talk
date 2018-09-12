(ns soul-talk.layout
  (:require [selmer.parser :as parser]
            [selmer.filters :as filters]
            [ring.util.http-response :refer [content-type ok]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))

(defn render
  " Additional information that is called csrf-token was in the html render"
  [template & [params]]
  (content-type
    (ok
      (parser/render-file
        template
        (assoc params
          :page template
          :csrf-token *anti-forgery-token*)))
    "text/html; charset=utf-8"))