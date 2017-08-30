(ns digger.level
  (:require [utils.core :refer [indexed]]))

; :diamond :bag :dug :filled

(defn create-level
  "Generate a new level with a specific width and height. Diamonds, bags and
  dug cells percetages are also specified and shouldn't total above 100,
  friendly advice."
  [width height diamond-pct bag-pct dug-pct]
  (let [diamonds (take diamond-pct (repeat :diamond))
        bags     (take bag-pct (repeat :bag))
        dugs     (take dug-pct (repeat :dug))
        filled   (take (- 100 diamond-pct bag-pct dug-pct) (repeat :filled))
        bucket   (concat diamonds bags dugs filled)]
    (take height
          (repeatedly
            (fn [] (take width (repeatedly #(conj #{} (rand-nth bucket)))))))))

(defn print-level
  "Print the given level, with '*' as diamonds, 'O' as bags, a space as dug
  cells and '-' as undug cells."
  [level]
  (doseq [row level]
    (doseq [cell row]
      (print (condp #(%1 %2) cell
               :diamond "*"
               :bag "O"
               :dug " "
               "-")))
    (println)))

(defn index-level [level]
  (indexed (map indexed level)))

