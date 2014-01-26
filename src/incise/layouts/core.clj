(ns incise.layouts.core
  (:require [incise.config :as conf])
  (:refer-clojure :exclude [get]))

(defonce layouts (atom {}))

(defn exists?
  "Check for the existance of a layout with the given name."
  [layout-with-name]
  (contains? @layouts (name layout-with-name)))

(defn get [layout-name & more]
  (apply @layouts (name layout-name) more))

(defn Parse->string
  "Take a Parse or parse-like map and pass it through the appropriate layout."
  [parse-data]
  (if-let [layout-key (:layout parse-data)]
    (if-let [layout-fn (get layout-key)]
      (layout-fn (conf/get) parse-data)
      (throw (ex-info (str "No layout function registered with key " layout-key)
                      {:layouts @layouts})))
    (throw (ex-info (str "No layout key specified in given parse.")
                    {:layouts @layouts
                     :parse parse-data}))))

(defn register
  "Register a layout function to a shortname.

  A layout function takes two arguments. The first is a snapshot of the global
  configuration. The second is some sort of context map. It should look
  something like Parse. The only required key is :layout which must be a
  shortname of a registered layout function."
  [short-names layout-fn]
  (swap! layouts
         merge (zipmap (map name (if (sequential? short-names)
                                   short-names
                                   [short-names]))
                       (repeat layout-fn))))
