(defproject local/scratch-clj "0.1.0-SNAPSHOT"

  :source-paths ["src"]
  :resource-paths ["resources"]
  :test-paths ["test"]
  :target-path "target"

  ;; :main ^:skip-aot scratch-clj.core

  :repositories [["releases" {:url "s3p://rjmetrics-private-m2-repository/releases"
                              :username :env
                              :passphrase :env
                              :snapshots false
                              :sign-releases false}]
                 ["snapshots" {:url "s3p://rjmetrics-private-m2-repository/snapshots"
                               :username :env
                               :passphrase :env
                               :snapshots true
                               :sign-releases false }]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [cheshire "5.6.1"]
                 [com.cognitect/transit-clj "0.8.285"]
                 [com.rjmetrics/database-clj "1.1.0"]
                 [log4j/log4j "1.2.17"]
                 [postgresql/postgresql "8.4-702.jdbc4"]
                 ]

  :plugins [[s3-wagon-private "1.1.2"]]

  :profiles {:uberjar {:aot :all}})
