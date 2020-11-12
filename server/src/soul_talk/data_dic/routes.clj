(ns soul-talk.data-dic.routes
  (:require [compojure.api.sweet :refer :all]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.data-dic.interface :as data-dic]))

(def private-routes
  (context "/data-dics" []
    :tags ["数据字典"]

    (GET "/" req
      :return Result
      :summary "全部字典数据"
      (data-dic/load-data-dic-page req))

    (POST "/" []
      :summary "新增"
      :body [data-dic data-dic/create-data-dic]
      (data-dic/save-data-dic data-dic))

    (PATCH "/" []
      :summary "更新"
      :body [data-dic data-dic/update-data-dic]
      (data-dic/update-data-dic data-dic))


    (DELETE "/:id" []
      :summary "删除"
      :path-params [id :- string?]
      (data-dic/delete-data-dic-by-id id))

    (GET "/:id" []
      :path-params [id :- string?]
      (data-dic/get-data-dic-by-id id))

    (GET "/pid/:pid" []
      :summary "通过父ID"
      :path-params [pid :- string?]
      (data-dic/load-data-dic-by-pid pid))

    ))
