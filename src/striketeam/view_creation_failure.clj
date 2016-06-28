(ns striketeam.view-creation-failure
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
            [com.rjmetrics.database.dialects :as dialects]
            [com.rjmetrics.database.ddl :as ddl]
            [com.rjmetrics.database.discovery :as discovery]
            [com.rjmetrics.database.exec :as exec]
            [com.rjmetrics.database.logging :as dblogging]
            [com.rjmetrics.database.table-spec :as spec]
            [com.rjmetrics.database.impl.redshift]
            )
  (:import  [org.postgresql.ds PGSimpleDataSource]))


(def dialect dialects/redshift)


(defn mk-datasource
  []
  (doto (PGSimpleDataSource.)
        (.setUser "test")
        (.setPassword "hJi0a8I6WuzEDwg5")
        (.setDatabaseName "test")
        (.setServerName "kdg-0624.cnn5pzderity.us-east-1.redshift.amazonaws.com")
        (.setPortNumber 5439)))


(defn mk-tablespec
  [table-name & colspecs]
  (let [pkey  (-> {}
                  (spec/column-name "pkey")
                  (spec/column-type :bigint))]
    (-> {}
        (spec/schema "test")
        (spec/table table-name)
        (spec/columns (into [pkey] colspecs)))))


(defn mk-colspecs
  [basename num-columns]
  (mapv #(-> {}
             (spec/column-name (str basename "_" %))
             (spec/column-type :bigint))
        (range 0 num-columns)))


(defn insert-rows
  [connection tablespec base-id]
  (exec/sql dialect connection
            (str "insert into " (spec/schema tablespec) "." (spec/table tablespec)
                 " (pkey) "
                 "values (" base-id ")"))

  (doseq [addend [1 2 4 8 16 32]]
    (exec/sql dialect connection
              (str "insert into " (spec/schema tablespec) "." (spec/table tablespec)
                   " (pkey) "
                   "select pkey + " addend
                   " from " (spec/schema tablespec) "." (spec/table tablespec)
                   " where pkey >= " base-id))
  )
)


(defn add-columns
  [connection tablespec basename num-columns]
  (jdbc/with-db-transaction [txn {:datasource (mk-datasource)}]
    (let [connection (:connection txn)]
      (doseq [colspec (mk-colspecs basename num-columns)]
        (ddl/add-column dialect connection tablespec colspec))
    )
  )
)


(defn recreate-view
  [connection orig-tablespec]
  (let [table-schema    (spec/schema orig-tablespec)
        table-name      (spec/table orig-tablespec)
        table-columns   (discovery/retrieve-column-specs dialect connection table-schema table-name)
        tablespec       (spec/columns orig-tablespec table-columns)
        view-name       (str table-name "_v")
        viewspec        (spec/table tablespec view-name)]

    (ddl/drop-view dialect connection viewspec {:if-exists true})
    (ddl/create-view dialect connection tablespec viewspec)
  )
)


(defn -main [& args]
  (binding [dblogging/*level* :debug]
    (with-open [connection  (.getConnection (mk-datasource))]
      (let     [tablespecs  (mapv mk-tablespec args)]

        (doseq [spec tablespecs]
          (ddl/create-table dialect connection spec)
          (insert-rows connection spec 1))

        (doseq [cn1 (range 1 9)
                spec tablespecs]
          (add-columns connection spec (str "added_" cn1) 4)
          (recreate-view connection spec)
        )
      )
    )
  )
)
