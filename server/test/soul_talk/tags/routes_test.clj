(ns soul-talk.tags.routes-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [soul-talk.tag.routes :refer :all]
            [taoensso.timbre :as log]))

(def context "/tags")
(def ^:dynamic *test-tag* (atom {:name "test-tag"}))

(deftest api-routes-test
  (testing "add tag "
    (let [name (str "test-tag" (rand-int 100))
          tag {:name name}
          resp (h/make-request-by-app-token
                 :post
                 (h/api-url context "/")
                 tag)
          body (h/body resp)
          tag (:tag body)]
      (is (= 200 (:status resp)))
      (reset! *test-tag* tag)))

  (testing "view tag"
    (let [id (:id @*test-tag*)
          resp (h/make-request-by-app-token
                 :get
                 (h/api-url context "/" id))
          body (h/body resp)
          tag (:tag body)]
      (is (= 200 (:status resp)))))

  (testing "get all tags page"
    (let [resp (h/make-request-by-app-token
                 :get
                 (h/api-url context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:tags body))))))
  )

(deftest site-routes-test
  (testing "get all tags page"
    (let [resp (h/make-request-by-login-token
                 :get
                 (h/site-uri context "/"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (< 0 (count (:tags body))))))

  (testing "delete tags by id"
    (let [id (:id @*test-tag*)
          resp (h/make-request-by-login-token
                 :delete
                 (h/site-uri context "/" id))
          body (h/body resp)]
      (is (= 200 (:status resp)))))
  )
