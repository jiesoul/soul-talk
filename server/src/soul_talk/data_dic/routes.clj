(ns soul-talk.data-dic.routes
  (:require [compojure.api.sweet :refer :all]
            [compojure.api.meta :refer [restructure-param]]
            [soul-talk.spec.core :refer [Result]]
            [soul-talk.data-dic.handler :as data-dic]
            [soul-talk.middleware :as m]))

(defmethod restructure-param :auth-app-key
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-app-key rule]))

(defmethod restructure-param :auth-login
  [_ rule acc]
  (update-in acc [:middleware] conj [m/wrap-auth rule]))


(def public-routes
  (context "/data-dices" []
    :tags ["数据字典"]

    (GET "/:id" []
      :auth-app-key #{"admin"}
      :path-params [id :- string?]
      :return Result
      (data-dic/get-data-dic-by-id id))

    (GET "/pid/:pid" []
      :auth-app-key #{"admin"}
      :summary "通过父ID"
      :return Result
      :path-params [pid :- string?]
      (data-dic/load-data-dices-by-pid pid))
    ))

(def private-routes
  (context "/data-dices" []
    :tags ["数据字典"]

    (GET "/" req
      :auth-login #{"admin"}
      :return Result
      :summary "全部字典数据"
      (data-dic/load-data-dic-page req))

    (POST "/" []
      :auth-login #{"admin"}
      :summary "新增"
      :return Result
      :body [data-dic data-dic/create-data-dic]
      (data-dic/save-data-dic data-dic))

    (PATCH "/" []
      :auth-login #{"admin"}
      :summary "更新"
      :return Result
      :body [data-dic data-dic/update-data-dic]
      (data-dic/update-data-dic data-dic))

    (GET "/:id" []
      :auth-login #{"admin"}
      :path-params [id :- string?]
      :return Result
      (data-dic/get-data-dic-by-id id))

    (GET "/pid/:pid" []
      :auth-login #{"admin"}
      :summary "通过父ID"
      :return Result
      :path-params [pid :- string?]
      (data-dic/load-data-dices-by-pid pid))


    (DELETE "/:id" []
      :auth-login #{"admin"}
      :summary "删除"
      :return Result
      :path-params [id :- string?]
      (data-dic/delete-data-dic-by-id id))

    ))
