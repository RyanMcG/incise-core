(ns incise.transformers.layout
  (:require [incise.config :as conf]
            [robert.hooke :refer [with-scope add-hook]]))

(declare ^:dynamic *site-options*)
(declare ^:dynamic *parse*)

(defmacro with-normalized-params [& body]
  `(let [[~'doc-string ~'destructuring ~'body]
         (if (string? ~'doc-string?)
           [~'doc-string? ~'destructuring ~'body]
           ["" ~'doc-string? (conj ~'body ~'destructuring)])]
     ~@body))

(defmacro deflayout
  "This is a helper macro for defining layout functions. A layout function at
  its core is just a function which takes two arguments:

    site-options - Global options for the site from incise.edn
    parse        - A Parse or map containing keys such as content and title

  This macro makes it just a little bit easier to define such functions by
  taking care of some boiler plate stuff like using the robert.hooke with-scope
  macro and binind the functions arguments to *site-options* and *parse* so
  partials can easily access them."
  [sym-name doc-string? destructuring & body]
  (with-normalized-params
    `(defn ~sym-name
       ~doc-string
       [parse#]
       (binding [*site-options* (conf/get)
                 *parse* parse#]
         (let [~destructuring [*site-options* *parse*]]
           (assoc parse# :content (with-scope ~@body)))))))

(defmacro defpartial
  "Defines a 'partial' which is basically a function with some default context."
  [sym-name doc-string? destructuring & body]
  (with-normalized-params
    `(defn ~sym-name
       ~doc-string
       [& args#]
       (let [~destructuring [*site-options* *parse* args#]]
         ~@body))))

(defmacro repartial
  "Replace the given var in a layout with a new function which will be called in
  its place and passed the result of the fn it is replacing. Returns an empty
  string so it can be used in layouts without issues."
  [to-be-replaced replacement]
  `(do
     (add-hook #'~to-be-replaced
               (fn [f# & args#] (~replacement (apply f# args#))))
     ""))

(defn- eval-with-context [code]
  (binding [*ns* (create-ns `user#)]
    (require '[clojure.core :refer :all])
    (require '[incise.transformers.layout :refer [*site-options* *parse*]])
    (eval `(do
             (def ~'parses (vals @incise.parsers.parse/parses))
             (def ~'tags (incise.utils/slot-by :tags ~'parses))
             ~@code))))

(defprotocol RenderableContent (render-content [content]))
(extend-protocol RenderableContent
  clojure.lang.PersistentList
  (render-content [content] (eval-with-context content))
  nil
  (render-content [content] "")
  Object
  (render-content [content] (str content)))

(defn use-layout
  "Use the given layout function by calling it with *site-options* and *parse*."
  [layout-fn] (:content (layout-fn *parse*)))
