(ns soul-talk.routes.site.article-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [soul-talk.utils.id-util :as id-utils]
            [clojure.tools.logging :as log]))

(defn site-uri [uri & opt]
  (apply str "/articles" uri opt))

(def ^:dynamic *article* (atom {:title       "test"
                                :body        "test"
                                :category_id 1
                                :description "test"
                                :create_by   1
                                :update_by   1}))

(def ^:dynamic *id* (atom nil))
(deftest site-article-test
  (testing "add article"
    (let [add-resp (h/make-request-by-login-token
                     :post
                     (site-uri "/")
                     @*article*)
          add-body (h/body add-resp)
          article  (:article add-body)]
      (is (= 200 (:status add-resp)))
      (reset! *id* (:id article))))

  (testing "update article"
    (let [article @*article*
          resp    (h/make-request-by-login-token
                    :patch
                    (site-uri "/")
                    (assoc article :id @*id* :title "test-update" :update_by 1))
          article (:article (h/body resp))]
      (is (= 200 (:status resp)))
      (is (= "test-update" (:title article)))
      (reset! *article* article)))

  (testing "view article"
    (let [article @*article*
          resp    (h/make-request-by-login-token
                    :get
                    (site-uri "/" (:id article)))
          body    (h/body resp)
          article (get body :article)]
      (is (= 200 (:status resp)))))

  (testing "public article "
    (let [id   @*id*
          resp (h/make-request-by-login-token
                 :patch
                 (site-uri "/" id "/publish"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (= "1101" (:publish (:article body))))))

  (testing "view articles page"
    (let [response (h/make-request-by-login-token
                     :get
                     (site-uri "/?title=test"))
          body     (h/body response)]
      (is (= 200 (:status response)))
      (is (< 0 (count (:articles body))))))

  (testing "delete article by id"
    (let [id   @*id*
          resp (h/make-request-by-login-token :delete (site-uri "/" id))]
      (is (= 200 (:status resp))))))


