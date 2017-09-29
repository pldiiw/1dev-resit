(ns digger.score
  (:require [digger.inventory :as inventory]
            [utils.math :as math]))

(def item-values (reduce
                  #(assoc %1 %2 (condp = %2
                                 :diamond 25
                                 :bag     500
                                 :shot    250
                                 :cherry  1000
                                 0))
                  {}
                  inventory/items))

(def ate-base 200)
(def ate-factor 2)

(defn compute [given-inventory]
  (reduce-kv #(+ %1 %3) 0 (update
                           (merge-with * given-inventory item-values)
                           :ate
                           #(* ate-factor (if (zero? %) 0 (math/expt 2 %))))))

(defn draw [scored]
  (println scored)) ; TODO: DRAW IT!
