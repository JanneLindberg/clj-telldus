(defproject telldus "0.1.0-SNAPSHOT"
  :description "Clojure library for communication with Telldus Tellstick daemon"
  :url "https://github.com/JanneLindberg/clj-telldus.git"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :javac-options ["-source" "1.7" "-target" "1.7" "-g"]
  :java-source-paths ["src/java"]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"]
                 ]
  :profiles {
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             })
