(ns utils)

(defn avg [& coll]
    (/ (apply + coll) (count coll))
)

(defn count-by-key
  [m]
  (map (fn [[k v]] {k (count v)}) m))
