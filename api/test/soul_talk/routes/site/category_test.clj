(ns soul-talk.routes.site.category-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [soul-talk.category.routes :refer :all]
            [clojure.tools.logging :as log]))

(def context "/categories")
(def ^:dynamic *test-category* (atom nil))

(deftest category-test

  (testing "add category "
    (let [name     (str "test-category" (rand-int 100))
          category {:name name :create_by 1 :update_by 1}
          resp     (h/make-request-by-login-token :post (h/site-uri context "/") category)
          body     (h/body resp)
          category (:category body)]
      (is (= 200 (:status resp)))
      (reset! *test-category* category)
      (log/debug "test-category: " @*test-category*)))

  (testing "get all categories page"
    (let [resp (h/make-request-by-login-token
                 :get
                 (h/site-uri context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:categories body))))))

  (testing "load a category by id"
    (log/debug "category: " @*test-category*)
    (let [id   (:id @*test-category*)
          resp (h/make-request-by-login-token :get (h/site-uri context "/" id))
          body (h/body resp)]
      (is (= 200 (:status resp)))))

  (testing "delete category by id"
    (let [id   (:id @*test-category*)
             resp (h/make-request-by-login-token
                    :delete
                    (h/site-uri context "/" id))
             body (h/body resp)]
         (is (= 200 (:status resp))))))
