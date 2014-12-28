(ns incise.parsers.impl.clj
  (:require (incise.parsers [core :refer [register]]
                            [html :refer [html-parser]])))

(defn clj-parse [code]
  (read-string (str \( code \))))

(def html-clj-parser (html-parser clj-parse))

(register [:clj] #'html-clj-parser)
