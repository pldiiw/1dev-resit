(ns digger.core
  (:gen-class)
  (:refer-clojure :exclude [update])
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [digger.level :as level]
            [digger.player :as player]))

(def size [640 400])
(def level-size [20 20])
(def cell-size (map #(/ %1 %2) size level-size))

(defn setup []
  (q/frame-rate 60)
  {:level (level/create level-size [1 3 10])
   :player (player/create [0 0] [60 60] 3)})

(defn update [state]
  (-> state
      (clojure.core/update
       ,,
       :level
       #(level/dig % (select-keys (:player state) [:x :y])))))

(defn draw [state]
  (q/background 0)
  (level/draw (:level state) cell-size)
  (player/draw (:player state) cell-size))

(defn key-pressed [state event]
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

