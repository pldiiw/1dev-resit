(ns digger.sprites
  (:require [quil.core :as q]))

;; Inventory of the differents sprites and their filepaths.
(def sprite-files {:dug     "resources/dug.png"
                   :filled  "resources/filled.png"
                   :diamond "resources/diamond.png"
                   :bag     "resources/bag.png"
                   :cherry  "resources/cherry.png"
                   :player  "resources/player.png"
                   :nobbin  "resources/nobbin.png"
                   :hobbin  "resources/hobbin.png"})

(defn load-all
  "Load all the sprites into a hash map."
  []
  (reduce-kv #(assoc %1 %2 (q/load-image %3)) {} sprite-files))

(defn resize-all
  "Resize all the sprites to a defined size."
  [sprite-map cell-size]
  (reduce-kv #(do (apply q/resize %3 cell-size)
                  (assoc %1 %2 %3))
              {}
              sprite-map))
