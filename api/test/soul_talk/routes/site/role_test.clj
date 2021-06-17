(ns soul-talk.routes.site.role-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [taoensso.timbre :as log]))

(def context "/roles")

(def ^:dynamic *role1* (atom {:name "测试" :create_by 1 :update_by 1 :menus-ids [10 11 12]}))

(deftest role-test
  (testing "add role {:name 测试}"
    (let [resp (h/make-request-by-login-token :post (h/site-uri context) @*role1*)
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (reset! *role1* (:role body))))

  (testing "update role"
    (let [name (str "测试更新" (rand-int 100))
          resp (h/make-request-by-login-token :patch (h/site-uri context) (assoc @*role1* :name name :update_by 1))
          body (h/body resp)]
      (is (= 200 (:status resp)))))

  (testing "view role by id"
    (let [id       (:id @*role1*)
          response (h/make-request-by-login-token
                     :get
                     (h/site-uri context "/" id))
          body     (h/body response)
          role     (:role body)]
      (is (= 200 (:status response)))))

  (testing "view roles menus by ids"
    (let [response (h/make-request-by-login-token
                     :get
                     (h/site-uri context "/menus" "?ids=11,12,13"))
          body     (h/body response)
          menus    (:menus body)]
      (is (= 200 (:status response)))
      (is (<= 0 (count menus)))))

  (testing "delete role "
    (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/" (:id @*role1*)))
          body (h/body resp)]
      (is (= 200 (:status resp))))))
