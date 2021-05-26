(ns soul-talk.routes.site.site-info-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]))

(deftest load-site-info
  (testing "load site info"
    (let [resp (h/make-request-by-login-token :get (str "/site-info/1"))
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (is (= 1 (:id (:site-info body)))))))

(def site-info {:id 1 :name "个人网站" :description "个人网站" :tags "clojure java UE4 UE5 Blender" :author "jiesoul"})

(deftest update-site-info
  (testing "update site info"
    (let [resp (h/make-request-by-login-token :patch (str "/site-info/1") site-info)
          body (h/body resp)]
      (is (= 200 (:status resp))))))


