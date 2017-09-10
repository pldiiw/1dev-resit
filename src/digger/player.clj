(ns digger.player
  (:require [quil.core :as q]))

(defn create
  [[x y :as coords] [move-cooldown shot-cooldown :as cooldowns] lives]
  {:x x :y y
   :move move-cooldown :shot shot-cooldown
   :lives lives})

(defn up [{y :y :as player}]
  (if (> y 0)
    (update player :y dec)
    player))

(defn left [{x :x :as player}]
  (if (> x 0)
    (update player :x dec)
    player))

(defn down [{y :y :as player} level-height]
  (if (< y (dec level-height))
    (update player :y inc)
    player))

(defn right [{x :x :as player} level-width]
  (if (< x (dec level-width))
    (update player :x inc)
    player))

(defn draw [{x :x y :y} [cw ch :as cell-size]]
  (q/fill 255 0 0)
  (apply q/ellipse (+ (* x cw) (/ cw 2)) (+ (* y ch) (/ ch 2)) cell-size))

(defn key-pressed [player key [level-width level-height]]
  (condp = key
   :z (up player)
   :d (right player level-width)
   :s (down player level-height)
   :q (left player)
   player))

