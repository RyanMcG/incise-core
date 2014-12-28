(ns incise.parsers.impl.html
  (:require (incise.parsers [core :as pc]
                            [html :refer [html-parser]])))

(def html-identiy-parser (html-parser identity))
(pc/register [:html :htm] #'html-identiy-parser)
