(ns soul-talk.core-test
  (:require [cljs.test :refer-macros [is testing deftest]]))

(deftest a-test
  (testing "i ok"
    (is (= 1 1))))

(deftest b-test
  (testing "i fail"
    (is (= 1 0))))