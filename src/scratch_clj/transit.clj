(ns scratch-clj.transit
  (:require [cognitect.transit :as transit]))


(defn safe-read
  "Attempts to read from a Transit stream, returning nil if at end-of-file.
  Note: not appropriate if you have nils in your stream.
  
    reader  An existing Transit reader object.
  "
  [reader]
  (try
    (transit/read reader)
    (catch RuntimeException ex
      (if (instance? java.io.EOFException (.getCause ex))
        nil
        (throw ex)))))


(defn transit-seq
  "Creates a lazy sequence from an inputstream containing transit data.
  Note: not appropriate if you have nils in your stream.
  
    in      The stream to read
    format  
  "
  [in format]
  (let [reader  (transit/reader in format)
        f       (fn seqfn []
                  (when-let [item (safe-read reader)]
                    (lazy-seq (cons item (seqfn)))))]
    (f)))

