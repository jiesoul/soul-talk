(ns soul-talk.data-dic.handler
  (:require [soul-talk.data-dic.db :as db]
            [soul-talk.utils :as utils]))

(defn load-all []
  (let [data-dics (db/load-all)]
    (utils/ok "加载成功" {:data-dics data-dics})))
