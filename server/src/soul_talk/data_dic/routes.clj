(ns soul-talk.data-dic.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.data-dic.interface :as data-dic]))

(def private-routes
  (context "/data-dics" []
    :tags ["数据字典"]
    (GET "/" []
      :return Result
      :summary "全部字典数据"
      (data-dic/load-all))

    ))
