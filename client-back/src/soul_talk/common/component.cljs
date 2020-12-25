(ns soul-talk.common.component
  (:require [reagent.core :as r]
            [reagent.impl.template :as rtpl]
            ["@material-ui/core" :as mui]))

(def ^:private input-component
  (r/reactify-component
    (fn [props]
      [:input (-> props
                (assoc :ref (:inputRef props))
                (dissoc :inputRef))])))

(def ^:private textarea-component
  (r/reactify-component
    (fn [props]
      [:textarea (-> props
                   (assoc :ref (:inputRef props))
                   (dissoc :inputRef))])))

(defn text-field [props & children]
  (let [props (-> props
                (assoc-in [:InputProps :inputComponent]
                  (cond
                    (and (:multiline props) (:rows props) (not (:maxRows props)))
                    textarea-component

                    ;; FIXME: Autosize multiline field is broken.
                    (:multiline props)
                    nil

                    ;; Select doesn't require cursor fix so default can be used.
                    (:select props)
                    nil

                    :else
                    input-component))
                rtpl/convert-prop-value)]
    (apply r/create-element mui/TextField props (map r/as-element children))))