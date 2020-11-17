(ns soul-talk.article.routes-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [taoensso.timbre :as log]))

(defn site-uri [uri & opt]
  (apply str "/articles" uri opt))

(def ^:dynamic *article* (atom {:title       "ass"
                                :body        "ass"
                                :description "ass"
                                :create_by   1}))

(deftest site-routes-test
  (testing "add article"
    (let [add-resp (h/make-request-by-login-token
                     :post
                     (site-uri "/")
                     @*article*)
          add-body (h/body add-resp)
          article (get add-body :article)]
      (is (= 200 (:status add-resp)))
      (is (= "ass" (:title article)))
      (reset! *article* article)))

  (testing "view article"
    (let [id (:id @*article*)
          resp (h/make-request-by-login-token
                 :get
                 (site-uri "/" id))
          body (h/body resp)
          article (get body :article)]
      (is (= 200 (:status resp)))
      (is (= "ass" (:title article)))))

  (testing "public article "
    (let [id           (:id @*article*)
          publish-resp (h/make-request-by-login-token
                         :patch
                         (site-uri "/" id "/publish"))
          publish-body (h/body publish-resp)]
      (log/info "article id: " id)
      (is (= 200 (:status publish-resp)))))

  (testing "update article"
    (let [id      (:id @*article*)
          title "sssss"
          article {:id id
                   :title title
                   :body "dddddddddd"
                   :update_by 1}
          resp    (h/make-request-by-login-token
                    :patch
                    (site-uri "/")
                    article)
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (= title (get-in body [:article :title])))))

  (testing "view articles page"
    (let [response (h/make-request-by-login-token
                     :get
                     (site-uri "/?title=?sss"))
          body     (h/body response)]
      (is (= 200 (:status response)))
      (is (< 0 (count (:articles body)))))
    )

  )