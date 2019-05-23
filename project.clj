(defproject incise-core "0.5.1"
  :description "A hopefully simplified static site generator in Clojure."
  :url "https://github.com/RyanMcG/incise"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [ring "1.7.1"]
                 [com.stuartsierra/component "0.3.0"]
                 [compojure "1.6.1"]
                 [http-kit "2.1.19"]
                 [robert/hooke "1.3.0"]
                 [org.clojure/java.classpath "0.3.0"]
                 [org.clojure/tools.nrepl "0.2.11"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [org.clojure/tools.cli "0.3.3"]
                 [pallet-map-merge "0.1.0"]
                 [enlive "1.1.6"]
                 [com.taoensso/timbre "3.3.1"]
                 [manners "0.8.0"]
                 [chic-text "0.2.0"]]
  :aliases {"spec" ["with-profile" "spec" "spec"]
            "test-and-spec" ["do" "spec," "test"]}
  :profiles {:spec {:dependencies [[speclj "3.3.1"]]
                    :test-paths ["spec"]
                    :plugins [[speclj "3.1.0"]]}}
  :repl-options {:init-ns incise.repl}
  :main incise.core)
