(ns soul-talk.pagination
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))


(def default-page 1)
(def default-pre-page 20)
(def min-page 1)
(def min-pre-page 1)
(def offset-key :offset)
(def page-key :page)
(def pre-page-key :pre-page)
(def prev-key :previous)
(def next-key :next)

(def default-pagination-params
  {page-key default-page pre-page-key default-pre-page})

(defn parser-number [s]
  (cond
    (isa? (type s) java.lang.Number) s
    (isa? (type s) java.lang.String)
    (if (str/blank? s)
      nil
      (Integer/parseInt s))
    :else nil))

(defn map-kv [m f]
  (reduce-kv #(assoc %1 %2 (f %3)) {} m))

(defn extract [request]
  (let [params (:params request)
        params (merge default-pagination-params params)
        pagination (select-keys params [page-key pre-page-key])]
    (map-kv pagination parser-number)))

(defn current-page [pagination]
  (let [paginate-params (extract pagination)]
    (max (page-key paginate-params) min-page)))

(defn next-page [pagination]
  (let [page (current-page pagination)]
    (inc page)))

(defn prev-page [pagination]
  (let [page (current-page pagination)]
    (max min-page (dec page))))

(defn pre-page [pagination]
  (let [paginate-params (extract pagination)]
    (max (pre-page-key paginate-params) min-pre-page)))

(defn offset [pagination]
  (* (dec (current-page pagination)) (pre-page pagination)))

(defn create [request]
  (let [page (current-page request)
        pre-page (pre-page request)
        offset (offset request)
        next-page (next-page request)
        prev-page (prev-page request)]
    {page-key page pre-page-key pre-page offset-key offset next-key next-page prev-key prev-page}))
