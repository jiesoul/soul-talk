(ns soul-talk.post-validate
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]
            [taoensso.timbre :as log]))

(defn post-errors [post]
  (first
    (b/validate
      post
      :title [[v/required :message "标题不能为空"]]
      :category [[v/required :message "请选择一个分类"]]
      :content [[v/required :message "内容不能为空"]])))