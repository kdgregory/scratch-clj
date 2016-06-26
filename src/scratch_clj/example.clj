(ns scratch-clj.example
  )

(defn- foo [x]
  (+ 2 x))

(defn -main [& argv]
  (println (map foo (range 10)))
  )