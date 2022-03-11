(ns portal-clerk-kindly-nrepl-1.kindly.util)

(defn map-coll->key-vector-map
  [coll]
  (reduce (fn [new-map key]
            (assoc new-map key (vec (map key coll))))
          {}
          (keys (first coll))))
