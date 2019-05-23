(ns incise.transformers.impl.html-header-anchors
  (:require [clojure.string :as s]
            [incise.transformers.core :refer [register]]
            [net.cgrand.enlive-html :as html]))

(defn- make-anchor [name]
  (html/html [:a.incise-generated-tag (sorted-map :name name :href (str \# name))]))

(defn- node->anchor [node]
  (-> node
      (html/text)
      (s/lower-case)
      (s/replace  #"\s" "-")
      (s/replace  #"[\"'\?]" "")
      (make-anchor)))

(defn- node-prefixer [prefix-fn]
  (fn [node]
    (assoc node
           :content
           (vec (concat (prefix-fn node)
                        (:content node))))))

(def ^:private prefix-with-anchor (node-prefixer node->anchor))

(defn prefix-headers-with-anchors
  "Takes a string of HTML and adds prefixes anchor tags to the contents of
  headers in it."
  [html-source-str]
  (html/sniptest html-source-str
                 [#{:h1 :h2 :h3 :h4 :h5 :h6}]
                 prefix-with-anchor))

(defn html-header-anchors [data]
  (update-in data [:content] prefix-headers-with-anchors))

(register :html-header-anchors html-header-anchors)
