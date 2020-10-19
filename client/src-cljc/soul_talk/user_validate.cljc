(ns soul-talk.user-validate
  (:require [bouncer.validators :as v]
            [bouncer.core :as b]))

(defn change-pass-errors [{:keys [pass-old pass-new] :as params}]
  (first
    (b/validate
      params
      :pass-old [[v/required :message "旧密码不能为空"]
                 [v/min-count 7 :message "旧密码至少8位"]]
      :pass-new [[v/required :message "新密码不能为空"]
                 [v/min-count 7 :message "新密码至少8 位"]
                 [not= pass-old :message "新密码不能和旧密码一样"]]
      :pass-confirm [[= pass-new :message "确认密码必须和新密码相同"]])))
