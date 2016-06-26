(ns scratch-clj.transformers
  "Testbed for the planned Sourcerer transformation code."
  (:import [java.text SimpleDateFormat ParseException])
  )


(defn date-tz-string [pattern]
  (let [parser (java.text.SimpleDateFormat. pattern)]
    (fn [value]
      (when value
        (try
          (.parse parser value)
          (catch ParseException ex
            value))))))


(defn date-epoch-millis []
  (fn [value]
    (when value
      (java.util.Date. value))))


(defn make-transformer [config-item]
  (let [f (ns-resolve *ns* (symbol "scratch-clj.transformers" (:function config-item)))
        x (apply f (:arguments config-item))]
    (fn [rec]
      (if (get-in rec (:path config-item))
        (update-in rec (:path config-item) x)
        rec))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; Testing ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
    

(def test-config [
  { :path       [:event :originalTimestamp]
    :function   "date-tz-string"
    :arguments  ["yyyy-MM-dd'T'HH:mm:ss.SSSSX"] }
  { :path       [:event :milliStamp]
    :function   "date-epoch-millis"
    :arguments  [] }
  { :path       [:bibbledy :bobbledy :boo]
    :function   "date-epoch-millis"
    :arguments  [] }
])


(def test-record {
  :foo {
    :originalTimestamp "foo!"
  }
  :event {
    :originalTimestamp  "2015-12-12T19:11:01.266Z"
    :milliStamp         86400000
  }
})


(def test-xforms (mapv make-transformer test-config))

(println (reduce (fn [rec f] (f rec)) test-record test-xforms))
