(ns digger.level
  (:refer-clojure :exclude [print])
  (:require [quil.core :as q]
            [utils.core :refer [indexed]]))

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
          (fn [] (vec (repeatedly width #(disj (conj #{} (rand-nth bucket)) :filled))))))))

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

(defn draw
  "Draw each cell and items of the level with its according sprite."
  [level sprite-map [w h]]
  (doseq [[j row] (index-level level)]
    (doseq [[i cell] row]
      (let [x (* i w)
            y (* j h)]
        (q/image (:filled sprite-map) x y)
        (doseq [item cell]
          (q/image (item sprite-map) x y))))))

(defn dig
  "Add a :dug keyword to cell at x y to mark it dug."
  [level {x :x y :y}]
  (update-in level [y x] #(conj % :dug)))

(defn items-at
  "Retrieves the items located at x y"
  [level {x :x y :y}]
  (vec (disj (get-in level [y x]) :dug)))

(defn cleanup
  "Removes the items at x y"
  [level {x :x y :y}]
  (update-in level [y x] #(disj % :bag :diamond :cherry)))
