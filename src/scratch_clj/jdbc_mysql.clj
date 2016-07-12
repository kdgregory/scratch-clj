(ns scratch-clj.jdbc-mysql
  (:require [clojure.java.jdbc :as jdbc]))

(def conn (jdbc/get-connection {
            :subprotocol "mysql"
            :subname "//127.0.0.1:3306"
            :user "test"
            :password "test" }))

(def dbmeta (.getMetaData conn))
