# reagent 

## 三种途径

### Form1
返回 HTML
```clojure
(defn greet
   [name]                    ;; data coming in is a string
   [:div "Hello " name])     ;; returns Hiccup (HTML)
```

:<> 用来返回多个元素 
```clojure
(defn right-component
   [name]
   [:<>
     [:div "Hello"]
     [:div name]])
```

### Form-2

```clojure
(defn outer 
  [a b c]            ;; <--- parameters
  ;;  ....
  (fn [a b c]        ;; <--- forgetting to repeat them, is a rookie mistake
    [:div
      (str a b c)]))
```

### Form-3

```clojure
(defn my-component
  [x y z]  
  (let [some (local but shared state)      ;; <-- closed over by lifecycle fns
        can  (go here)]   
     (reagent/create-class                 ;; <-- expects a map of functions 
       {:display-name  "my-component"      ;; for more helpful warnings & errors

        :component-did-mount               ;; the name of a lifecycle function
        (fn [this] 
          (println "component-did-mount")) ;; your implementation
         
        :component-did-update              ;; the name of a lifecycle function
        (fn [this old-argv]                ;; reagent provides you the entire "argv", not just the "props"
          (let [new-argv (rest (reagent/argv this))]
            (do-something new-argv old-argv)))
      
        ;; other lifecycle funcs can go in here
     

        :reagent-render        ;; Note:  is not :render
         (fn [x y z]           ;; remember to repeat parameters
            [:div (str x " " y " " z)])})))

(reagent/render-component 
    [my-component 1 2 3]         ;; pass in x y z
    (.-body js/document))

;; or as a child in a larger Reagent component

(defn homepage []
  [:div
   [:h1 "Welcome"]
   [my-component 1 2 3]]) ;; Be sure to put the Reagent class in square brackets to force it to render!
```