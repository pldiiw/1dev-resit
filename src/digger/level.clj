(ns digger.level
  (:refer-clojure :exclude [print])
  (:require [quil.core :as q]
            [utils.core :refer [indexed]]))

; :diamond :bag :dug :filled

(defn create
  "Generate a new level with a specific width and height. Diamonds, bags and
  dug cells percetages are also specified and shouldn't total above 100,
  friendly advice."
  [[width height :as size] [diamond-pct bag-pct dug-pct :as percentages]]
  (let [diamonds (take diamond-pct (repeat :diamond))
        bags     (take bag-pct (repeat :bag))
        dugs     (take dug-pct (repeat :dug))
        filled   (take (- 100 diamond-pct bag-pct dug-pct) (repeat :filled))
        bucket   (concat diamonds bags dugs filled)]
    (vec (repeatedly
          height
          (fn [] (vec (repeatedly width #(conj #{} (rand-nth bucket)))))))))

(defn print
  "Print the given level, with '*' as diamonds, 'O' as bags, a space as dug
  cells and '-' as undug cells."
  [level]
  (doseq [row level]
    (doseq [cell row]
      (clojure.core/print (condp #(%1 %2) cell)
               :diamond "*"
               :bag "O"
               :dug " "
               "-"))
    (println)))

(defn index-level [level]
  (indexed (map indexed level)))

(defn draw-cells-via-fn [level cell-size cell-drawing-function]
  (doseq [[j row] (index-level level)]
    (doseq [[i cell] row]
      (let [x (* i (first cell-size))
            y (* j (last cell-size))]
        (cell-drawing-function x y cell)))))

(defn draw-filled [level cell-size]
  (draw-cells-via-fn
   level
   cell-size
   (fn [x y cell]
     (q/fill 0)
     (if (:filled cell) (apply q/rect x y cell-size)))))

(defn draw-dug [level cell-size]
  (draw-cells-via-fn
   level
   cell-size
   (fn [x y cell]
     (q/fill 255)
     (if (:dug cell) (apply q/rect x y cell-size)))))

(defn draw-diamonds [level cell-size]
  (draw-cells-via-fn
   level
   cell-size
   (fn [x y cell]
     (q/fill 0 0 255)
     (let [[center-x center-y] (map #(+ %1 (/ %2 2)) [x y] cell-size)]
       (if (:diamond cell) (apply q/ellipse center-x center-y (map #(/ % 2) cell-size)))))))

(defn draw-bags [level cell-size]
  (draw-cells-via-fn
   level
   cell-size
   (fn [x y cell]
     (q/fill 0 255 0)
     (let [[center-x center-y] (map #(+ %1 (/ %2 2)) [x y] cell-size)]
       (if (:bag cell) (apply q/ellipse center-x center-y (map #(/ % 2) cell-size)))))))

(defn draw [level cell-size]
  (draw-filled level cell-size)
  (draw-dug level cell-size)
  (draw-diamonds level cell-size)
  (draw-bags level cell-size))

(defn dig [level {x :x y :y}]
  (update-in level [y x] #(conj (disj % :filled) :dug)))
