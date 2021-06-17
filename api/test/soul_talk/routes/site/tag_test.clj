(ns soul-talk.routes.site.tag-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [soul-talk.tag.routes :refer :all]
            [clojure.tools.logging :as log]))

(def context "/tags")
(def ^:dynamic *test-tag* (atom {:name "test-tag"}))

(deftest tag-test
  (testing "add tag "
    (let [name (str "test-tag" (rand-int 100))
          tag  {:name name}
          resp (h/make-request-by-login-token :post (h/site-uri context "/") tag)
          body (h/body resp)
          tag  (:tag body)]
      (is (= 200 (:status resp)))
      (reset! *test-tag* tag)))

  (testing "get all tags page"
    (let [resp (h/make-request-by-login-token
                 :get
                 (h/site-uri context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:tags body))))))

  (testing "load a tag by id"
    (let [id   (:id @*test-tag*)
          resp (h/make-request-by-login-token :get (h/site-uri context "/" id))
          body (h/body resp)]
      (is (= 200 (:status resp)))))

  (testing "delete tags by id"
    (let [id   (:id @*test-tag*)
          resp (h/make-request-by-login-token
                 :delete
                 (h/site-uri context "/" id))
          body (h/body resp)]
      (is (= 200 (:status resp))))))
