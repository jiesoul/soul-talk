(ns soul-talk.data-dic.interface
  (:require [soul-talk.data-dic.handler :as handler]
            [soul-talk.data-dic.spec :as spec]))

(defn load-all []
  (handler/load-all))