(ns user
  (:require [soul-talk.models.db :as db :refer [db-spec]]))

(defn dev []
  (require 'dev)
  (in-ns 'dev))
