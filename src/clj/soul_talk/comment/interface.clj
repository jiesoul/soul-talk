(ns soul-talk.comment.interface
  (:require [soul-talk.comment.handler :as handler]))

(defn get-comments-by-articleId [id]
  (handler/get-comments-by-articleId id))
