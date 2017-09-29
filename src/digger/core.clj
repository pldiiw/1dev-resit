(ns digger.core
  (:gen-class)
  (:refer-clojure :exclude [update])
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [digger.level :as level]
            [digger.sprites :as sprites]
            [digger.player :as player]
            [digger.enemy :as enemy]
            [digger.score :as score]
            [digger.inventory :as inventory]))

(def size [640 640])
(def level-size [20 20])
(def cell-size (map #(/ %1 %2) size level-size))
(def max-enemies 5)
(def enemy-spawn-interval 300)
(def start-coords [0 0])

(defn setup 
  "Set framerate and initialize game state."
  []
  (q/frame-rate 60)
  {:level     (level/create level-size [1 3 10])
   :sprites   (sprites/resize-all (sprites/load-all) cell-size)
   :player    (player/create start-coords [60 60] 3)
   :enemies   []
   :inventory (inventory/create)
   :score     0})

(defn reset-stage 
  "Generate a new level and reset enemies."
  [state]
  (when (zero? (:lives (:player state))) (System/exit 0))
  (-> state
      (clojure.core/update :player  #(-> % (assoc :x 0) (assoc :y 0)))
      (assoc               :enemies [])
      (assoc               :level   (level/create level-size [1 3 10]))))

(defn update
  "Executed at each frame. Order each changes needed to be made to the state."
  [state]
  (let [player-coords  (select-keys (:player state) [:x :y])
        enemies-coords (map #(select-keys % [:x :y :type]) (:enemies state))
        nobbins-coords  (filter #(identical? (:type %) :nobbin) enemies-coords)
        hobbins-coords  (filter #(identical? (:type %) :hobbin) enemies-coords)]
    ;; Check if player has been hit by an enemy, if so, he loses a life and the
    ;; level is reset.
    (-> state
        ((fn [state]
           (if (pos? (player/check-hits player-coords enemies-coords))
             (-> state
                 (clojure.core/update :player  #(clojure.core/update % :lives dec))
                 (reset-stage))
             state)))
        ;; Dig the cells the player and hobbins are on.
        (clojure.core/update :level     #(reduce level/dig % (conj hobbins-coords player-coords)))
        ;; Add the items the cell the player is onto contains to the player's inventory.
        (clojure.core/update :inventory #(inventory/add % (level/items-at (:level state) player-coords)))
        ;; Remove the items from the cells the player and the enemies are on.
        (clojure.core/update :level     #(reduce level/cleanup % (conj enemies-coords player-coords)))
        ;; Calculate the score based on what's inside the player's inventory.
        (assoc               :score     (score/compute (:inventory state)))
        ;; Spawn a new enemy based on the following conditions.
        (clojure.core/update :enemies   #(if
                                           (and (< 60 (q/frame-count))
                                                (< (count %) max-enemies)
                                                (zero? (mod (q/frame-count) enemy-spawn-interval)))
                                           (conj % (enemy/create start-coords))
                                           %))
        ;; Move the enemies toward the player
        (clojure.core/update :enemies   #(map (fn [e] (enemy/move e player-coords (:level state))) %))
        ;; Try to morph every nobbin to hobbin
        (clojure.core/update :enemies   #(map enemy/try-morph-to-hobbin %)))))

(defn draw
  "After update, it's time to draw our entities on the screen."
  [state]
  (q/background 0)
  (let [s (:sprites state)]
    (level/draw (:level state) s cell-size)
    (player/draw (:player state) (:player s) cell-size)
    (doseq [e (:enemies state)] (enemy/draw e ((:type e) s) cell-size))
    (score/draw (:score state))))

(defn key-pressed
  "Executed each time the player presses a key."
  [state event]
  (let [key (:key event)]
    (clojure.core/update
     state
     :player
     #(player/key-pressed % key level-size))))

(defn -main [& args]
  (q/defsketch game
    :title "Digger"
    :middleware [m/fun-mode]
    :settings #(q/smooth 2)
    :setup setup
    :update update
    :draw draw
    :key-pressed key-pressed
    :size size))
