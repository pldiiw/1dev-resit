(ns digger.draw
  (:require [quil.core :as q]
            [digger.level :refer [index-level]]))

(declare draw-level
         draw-filled
         draw-dug
         draw-diamonds
         draw-bags
         draw-cells-via-fn)

(defn draw-level [level cell-size]
  (draw-filled level cell-size)
  (draw-dug level cell-size)
  (draw-diamonds level cell-size)
  (draw-bags level cell-size))

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

(defn draw-cells-via-fn [level cell-size cell-drawing-function]
  (doseq [[j row] (index-level level)]
    (doseq [[i cell] row]
      (let [x (* i (first cell-size))
            y (* j (last cell-size))]
        (cell-drawing-function x y cell)))))

