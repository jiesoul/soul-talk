(ns soul-talk.utils.id-util
  (:require [clj-time.coerce :as chronos]
            [soul-talk.utils :as utils]
            [flake.core :as flake]
            [taoensso.timbre :as log]))

(defn generate-id! []
  (flake/init!)
  (flake/flake->bigint (flake/generate!)))

(defonce bit-lengths {:timestamp 16 :cluster 2 :machine 2 :sequence-number 12})

(defonce bit-positions {:timestamp (+ (:cluster bit-lengths) (:machine bit-lengths) (:sequence-number bit-lengths))
                        :cluster (+ (:machine bit-lengths) (:sequence-number bit-lengths))
                        :machine (:sequence-number bit-lengths)
                        :sequence-number 0})

(defonce max-sequence-number (dec (bit-shift-left 1 (:sequence-number bit-lengths))))

(defonce epoch (chronos/to-long "2021-01-01"))

(defn- generate-cluster-id [] 1)

(defn- generate-machine-id [] 1)

(defn- generate-full-machine-id []
  (let [cluster-id (generate-cluster-id)
        machine-id (generate-machine-id)]
    (+ (bit-shift-left cluster-id (:machine bit-lengths)) machine-id)))

(defonce full-machine-id (generate-full-machine-id))

(defonce full-machine-chunk (bit-shift-left full-machine-id (:machine bit-positions)))

(def real-timestamp (ref 0))

(def sequence-number (ref 0))

(defn now-real []
  (System/currentTimeMillis))

(defn- now-test []
  epoch)

(defn- create-id [timestamp-id sequence-number-id]
  (let [time-id (- timestamp-id epoch)
        time-chunk (bit-shift-left time-id (:timestamp bit-positions))]
    (+ time-chunk full-machine-chunk sequence-number-id)))

(defn generate-id
  [now]
  (dosync
    (let [current-timestamp (now)]
      (if (= current-timestamp (ensure real-timestamp))
        (if (> (ensure sequence-number) max-sequence-number)
          (do
            (ref-set sequence-number 1)
            (let [final-timestamp (loop []
                                    (let [fresh-timestamp (now)]
                                      (if (> fresh-timestamp current-timestamp) fresh-timestamp (recur))))]
              (ref-set real-timestamp final-timestamp))
            (create-id (ensure real-timestamp) 0))
          (let [sequence-value (ensure sequence-number)]
            (alter sequence-number inc)
            (create-id current-timestamp sequence-value)))
        (do
          (ref-set sequence-number 1)
          (ref-set real-timestamp current-timestamp)
          (create-id current-timestamp 0))))))

(defn gen-id! []
  (generate-id now-real))