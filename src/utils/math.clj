(ns utils.math)

(defn expt [base pow]
  (apply * (repeat pow base)))
