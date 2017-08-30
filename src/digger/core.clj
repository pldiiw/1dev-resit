(ns digger.core
  (:gen-class)
  (:refer-clojure :exclude [update])
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [digger.level :refer [create-level]]
            [digger.draw :refer [draw-level]]))

(declare -main
         setup
         update
         draw
         key-pressed)

(def size [640 400])
(def level-size [20 20])
(def cell-size (map #(/ %1 %2) size level-size))

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

(defn setup []
  (q/frame-rate 60)
  {:level (create-level 20 20 1 3 10)})

(defn update [state]
  state)

(defn draw [state]
  (q/background 0)
  (draw-level (:level state) cell-size))

(defn key-pressed [state event]
  state)

