(ns digger.enemy
  (:require [quil.core :as q]))

;; The {n,h}obbins will be able to move each their frame countdown reaches
;; zero.
(def nobbin-move-cooldown 30)
(def hobbin-move-cooldown 20)
;; Frames before a nobbin transforms itself into a hobbin.
(def morph-countdown 1800)

(defn create
  "Return a map representing the hobbin.";
  [[x y]]
  {:x x
   :y y
   :move nobbin-move-cooldown
   :morph morph-countdown
   :type :nobbin})

(defn determine-weight [from to target]
  "Weighten the fact the that the distance 'from' to 'target' is longer than
  'to' to 'target'"
  (if (> (Math/abs (- from target)) (Math/abs (- to target)))
    0.5
    0))

(defn determine-validity
  "Determine if the given cell is valid for a move (i.e. not out of bounds
  and that is it dug for nobbins)."
  [cell type]
  (if (or (nil? cell) (and (identical? type :nobbin) (not (contains? cell :dug))))
    -10
    0))

(defn pick-prob
  "Given a direction, pick a weighted pseudo random number between 0 and 1."
  [direction [x y] type [px py] level]
  (condp = direction
    :up     (+ (rand) (determine-weight y (dec y) py) (determine-validity (get-in level [(dec y) x]) type))
    :down   (+ (rand) (determine-weight y (inc y) py) (determine-validity (get-in level [(inc y) x]) type))
    :left   (+ (rand) (determine-weight x (dec x) px) (determine-validity (get-in level [y (dec x)]) type))
    :right  (+ (rand) (determine-weight x (inc x) px) (determine-validity (get-in level [y (inc x)]) type))))

(defn choose-move-direction
  "Given coords, a type (nobbin or hobbin), the coords of a target and the
  current level, choose the move that will make the enemy closer to the target."
  [e-coords type p-coords level]
  (let [directions [:up :down :left :right]
        probs      (map #(pick-prob % e-coords type p-coords level) [:up :down :left :right])
        hashed     (conj (zipmap directions probs) {:else 0})
        direction  (first (apply max-key val hashed))]
    direction))

(defn move
  "Move the enemy to a random adjacent cell that will make him closer to the
  player."
  [{x :x y :y move-cooldown :move type :type :as e} {px :x py :y} level]
  (if (zero? move-cooldown)
    (let [direction (choose-move-direction [x y] type [px py] level)]
      (-> e
          (assoc :move (if (= (:type e) :nobbin) nobbin-move-cooldown hobbin-move-cooldown))
          (assoc :x    (condp = direction :left (dec x) :right (inc x) x))
          (assoc :y    (condp = direction :up   (dec y) :down  (inc y) y))))
    (update e :move dec)))

(defn try-morph-to-hobbin
  "If the morph countdown of the given enemy reaches 0, change its type to
  hobbin."
  [e]
  (cond
    (and (identical? (:type e) :nobbin) (zero? (:morph e))) (assoc e :type :hobbin)
    (identical? (:type e) :hobbin)                          e
    :else                                                   (update e :morph dec)))

(defn draw[{x :x y :y} sprite [w h]]
  (q/image sprite (* x w) (* y h)))
