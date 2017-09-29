(ns digger.inventory)

"All the items a cell can contain."
(def items [:diamond :bag :shot :cherry :ate])

(defn create []
  (reduce #(assoc %1 %2 0) {} items))

(defn add
  "Add items to the given inventory"
  [inventory items-to-add]
  (reduce (fn [m k] (update m k #(inc %))) inventory items-to-add))
