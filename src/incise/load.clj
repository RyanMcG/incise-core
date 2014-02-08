(ns incise.load
  (:require [incise.config :as conf]
            [clojure.java.classpath :refer [classpath]]
            [clojure.tools.namespace.find :as ns-tools]))

(defn- ns-predicate-from-regex [regex]
  (fn [namespace-sym]
    (re-find regex (str namespace-sym))))

(defn- get-namespaces-from-classpath []
  (ns-tools/find-namespaces (classpath)))

(defn- filter-namespaces-with-fn
  "Find symbols for namespaces on the classpath that pass the given filter
  function."
  [filter-fn]
  (->> (get-namespaces-from-classpath)
       (remove (ns-predicate-from-regex #"-(test|spec)$"))
       (filter filter-fn)))

(defn- require-sym [ns-sym]
  (require :reload ns-sym)
  ns-sym)

(defn- load-ns-syms
  "Require with reload all namespaces returned by the given fn."
  [ns-syms-fn]
  (doall (map require-sym (ns-syms-fn))))

(defmacro ^:private defloader [plural-sym regex]
  (let [fn-name (symbol (str 'load- plural-sym))]
    `(def ~fn-name
       (partial load-ns-syms
                (partial filter-namespaces-with-fn
                         (ns-predicate-from-regex ~regex))))))

(defloader parsers-and-layouts #"incise\.(layouts|parsers)\.impl\..+")
(defloader deployers #"incise\.deployer\.impl\..+")
(defloader once-fixtures #"incise\.once\.fixtures\.impl\..+")
(defloader middlewares #"incise\.middlewares\.impl\..+")
