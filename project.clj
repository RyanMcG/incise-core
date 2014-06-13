(defproject incise-core "0.4.0"
  :description "A hopefully simplified static site generator in Clojure."
  :url "https://github.com/RyanMcG/incise"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.3.0-RC1"]
                 [com.stuartsierra/component "0.2.1"]
                 [compojure "1.1.6"]
                 [http-kit "2.1.18"]
                 [robert/hooke "1.3.0"]
                 [org.clojure/java.classpath "0.2.2"]
                 [org.clojure/tools.nrepl "0.2.3"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [org.clojure/tools.cli "0.3.1"]
                 [pallet-map-merge "0.1.0"]
                 [enlive "1.1.5"]
                 [clj-time "0.6.0"]
                 [com.taoensso/timbre "3.1.6"]
                 [manners "0.6.0"]]
  :profiles {:dev {:dependencies [[speclj "2.9.1"]]}}
  :repl-options {:init-ns incise.repl}
  :plugins [[speclj "2.9.1"]]
  :test-paths ["spec/"]
  :main incise.core)
