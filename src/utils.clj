(ns utils)

(defn avg [& coll]
    (/ (apply + coll) (count coll))
)

