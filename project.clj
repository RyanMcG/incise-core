(defproject incise-core "0.5.0"
  :description "A hopefully simplified static site generator in Clojure."
  :url "https://github.com/RyanMcG/incise"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.2"]
                 [com.stuartsierra/component "0.2.2"]
                 [compojure "1.3.1"]
                 [http-kit "2.1.19"]
                 [robert/hooke "1.3.0"]
                 [org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.nrepl "0.2.6"]
                 [org.clojure/tools.namespace "0.2.8"]
                 [org.clojure/tools.cli "0.3.1"]
                 [pallet-map-merge "0.1.0"]
                 [enlive "1.1.5"]
                 [clj-time "0.9.0"]
                 [com.taoensso/timbre "3.3.1"]
                 [manners "0.6.0"]
                 [chic-text/chic-text.terminal "0.1.0"]]
  :aliases {"spec-all" ["with-profile" "spec:spec,1.5:spec,1.7" "spec"]
            "test-all" ["with-profile" "test:test,1.5:test,1.7" "test"]
            "test-and-spec" ["do" "spec-all," "test-all"]
            "test-ancient" "test-and-spec"}
  :profiles {:spec {:dependencies [[speclj "3.1.0"]]
                    :test-paths ["spec"]
                    :plugins [[speclj "3.1.0"]]}
             :1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0-alpha4"]]}}
  :repl-options {:init-ns incise.repl}
  :main incise.core)
