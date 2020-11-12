(ns soul-talk.pagination
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

;;定义一些默认值和 key
(def default-page 1)
(def default-pre-page 20)
(def min-page 1)
(def min-pre-page 1)
(def offset-key :offset)
(def page-key :page)
(def per-page-key :per_page)
(def prev-key :previous)
(def next-key :next)
(def total-key :total)
(def total-pages-key :total_pages)

;;默认值
(def default-pagination-params
  {page-key default-page per-page-key default-pre-page})

;; 把 string 转换为 数字
(defn parser-number [s]
  (cond
    (isa? (type s) java.lang.Number) s
    (isa? (type s) java.lang.String)
    (if (str/blank? s)
      nil
      (Integer/parseInt s))
    :else nil))

;; 对 map 的每一项应用函数
(defn map-kv [m f]
  (reduce-kv #(assoc %1 %2 (f %3)) {} m))

;; 从请求中提取分页参数
(defn extract [request]
  (let [params (:params request)
        params (merge default-pagination-params params)
        pagination (select-keys params [page-key per-page-key])]
    (map-kv pagination parser-number)))

;;计算当前页
(defn current-page [pagination]
  (let [paginate-params (extract pagination)]
    (max (page-key paginate-params) min-page)))

;;计算下一页
(defn next-page [pagination]
  (let [page (current-page pagination)]
    (inc page)))

;; 上一页
(defn prev-page [pagination]
  (let [page (current-page pagination)]
    (max min-page (dec page))))

;; 每页显示多少条
(defn pre-page [pagination]
  (let [paginate-params (extract pagination)]
    (max (per-page-key paginate-params) min-pre-page)))

;; 记录的开始值
(defn offset [pagination]
  (* (dec (current-page pagination)) (pre-page pagination)))

;; 创建分页 map
(defn create [request]
  (let [page (current-page request)
        pre-page (pre-page request)
        offset (offset request)
        next-page (next-page request)
        prev-page (prev-page request)]
    {page-key page per-page-key pre-page offset-key offset next-key next-page prev-key prev-page}))

;; 计算总页数
(defn total-pages [total pre-page]
  (let [total-pages (quot total pre-page)
        rem (rem total pre-page)]
    (if (zero? rem)
      total-pages
      (inc total-pages))))

;; 根据总记录数生成最终的分页 map
(defn create-total [pagination total]
  (let [total-pages (total-pages total (per-page-key pagination))]
    (-> pagination
        (assoc total-key total)
        (assoc total-pages-key total-pages))))
