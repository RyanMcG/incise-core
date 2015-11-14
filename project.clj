(defproject incise-core "0.5.1"
  :description "A hopefully simplified static site generator in Clojure."
  :url "https://github.com/RyanMcG/incise"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [com.stuartsierra/component "0.3.0"]
                 [compojure "1.4.0"]
                 [http-kit "2.1.19"]
                 [robert/hooke "1.3.0"]
                 [org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.nrepl "0.2.11"]
                 [org.clojure/tools.namespace "0.2.10"]
                 [org.clojure/tools.cli "0.3.3"]
                 [pallet-map-merge "0.1.0"]
                 [enlive "1.1.6"]
                 [clj-time "0.11.0"]
                 [com.taoensso/timbre "3.3.1"]
                 [manners "0.8.0"]
                 [chic-text "0.2.0"]]
  :aliases {"spec-all" ["with-profile" "spec:spec,1.5:spec,1.7" "spec"]
            "test-all" ["with-profile" "test:test,1.5:test,1.7" "test"]
            "test-and-spec" ["do" "spec-all," "test-all"]
            "test-ancient" "test-and-spec"}
  :profiles {:spec {:dependencies [[speclj "3.3.1"]]
                    :test-paths ["spec"]
                    :plugins [[speclj "3.1.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0-alpha5"]]}}
  :repl-options {:init-ns incise.repl}
  :main incise.core)
