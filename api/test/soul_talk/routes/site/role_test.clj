(ns soul-talk.routes.site.role-test
  (:require [clojure.test :refer :all]
            [soul-talk.helper :as h]
            [taoensso.timbre :as log]))

(def context "/roles")

(def ^:dynamic *role1* (atom {:name "测试" :create_by 1 :update_by 1 :menus-ids [10 11 12]}))

(deftest add-role
  (testing "add role {:name 测试}"
    (let [resp (h/make-request-by-login-token :post (h/site-uri context) @*role1*)
          body (h/body resp)]
      (is (= 200 (:status resp)))
      (log/debug "role: " (:role body))
      (reset! *role1* (:role body)))))

(deftest update-role
  (testing "update role"
    (let [name (str "测试更新" (rand-int 100))
          _ (log/debug "*role1: " @*role1*)
          resp (h/make-request-by-login-token :patch (h/site-uri context) (assoc @*role1* :name name :update_by 1))
          body (h/body resp)]
      (is (= 200 (:status resp))))))

(deftest get-role-by-id
  (testing "view role by id"
    (let [id (:id @*role1*)
          response (h/make-request-by-login-token
                     :get
                     (h/site-uri context "/" id))
          body     (h/body response)
          role (:role body)]
      (is (= 200 (:status response))))))

(deftest get-role-menus-by-ids
  (testing "view roles menus by ids"
    (let [response   (h/make-request-by-login-token
                       :get
                       (h/site-uri context "/menus" "?ids=11,12,13"))
          body       (h/body response)
          menus (:menus body)]
      (is (= 200 (:status response)))
      (is (<= 0 (count menus))))))

(deftest delete-role
  (testing "delete role "
    (let [resp (h/make-request-by-login-token :delete (h/site-uri context "/" (:id @*role1*)))
          body (h/body resp)]
      (is (= 200 (:status resp))))))
