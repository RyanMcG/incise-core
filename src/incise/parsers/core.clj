(ns incise.parsers.core
  (:require [incise.parsers.utils :refer [extension]]
            (manners [really :refer [really]]
                     [victorian :refer [avow]])
            (incise [config :as conf]
                    [utils :refer [directory? log-file]])
            [clojure.java.io :refer [file]]
            [taoensso.timbre :refer [report color-str trace]])
  (:import [java.io File]))

(defonce ^{:doc "An atom containing a mapping of extensions (strings) to parse
                functions. A parse function takes a java.io.File and returns
                either a thunk or delay, which when invoked returns a sequence
                of files. This two step invocation is necessary to achieve
                features like tags."}
  parsers
  (atom {}))

(defn register
  "Register a parser for the given file extensions."
  [extensions parser]
  (swap! parsers
         merge (zipmap (map name (if (sequential? extensions)
                                   extensions
                                   [extensions]))
                       (repeat parser))))

(defn register-mappings!
  "Takes a map of custom parser mappings and applies them to the parsers map."
  [mappings]
  (doseq [[function-key new-function-key] mappings]
    (register new-function-key (@parsers (name function-key)))))

(defn- ->thunk
  "Coerce the given delay or thunk to a thunk."
  [delay-or-fn]
  {:pre [((some-fn fn? delay?) delay-or-fn)]}
  (if (delay? delay-or-fn)
    (fn [] (force delay-or-fn))
    delay-or-fn))

(defn parse
  "Returns a thunk which when invoked will return a list of files written from
  the invoked parser. The thunk will have some source-file meta. It may also
  return nil if a parser for the given file is not found or the parser returns
  nil instead of a dealy or thunk."
  [^File handle]
  {:pre [(instance? File handle)]}
  (when-let [mappings (conf/get :custom-parser-mappings)]
    (register-mappings! mappings))
  (let [ext (extension handle)
        current-parsers @parsers]
    (when-let [parser (current-parsers ext)]
      (report (color-str :blue "Parsing") (.getPath handle))
      (trace "using parser:" parser)
      (let [thunk-or-delay (parser handle)]
        (avow "parser thunk"
              [[(some-fn nil? delay? fn?)
                (format (str "the result (%s) of the parser (%s) called on the "
                             "file (\"%s\") must be nil, a delay, or a zero "
                             "arity function.")
                        (pr-str thunk-or-delay) parser handle)]]
              thunk-or-delay)
        (if thunk-or-delay
          (with-meta (->thunk thunk-or-delay) {:source-file handle}))))))

(defn input-file-seq
  "Returns a sequence of files (exclusing directories) from the input
  directory."
  []
  (->> (conf/get :in-dir)
       (file)
       (file-seq)
       (remove directory?)))

(defn- invoke [parsing-thunk]
  (let [handle (-> parsing-thunk meta :source-file)
        generated-files (parsing-thunk)]
    (doseq [f generated-files] (log-file :green "Generated" f))
    [handle generated-files]))

(defn parse-all
  "Parse all of the given files completely by calling parse on each one and
  invoking the parse result."
  [files]
  (->> files
       (pmap parse)
       (keep identity)
       (doall) ; Ensure that all files have been parsed.
       (pmap invoke)
       (into {})))

(defn parse-all-input-files
  "Completely parse all of the files from the input directory."
  []
  (parse-all (input-file-seq)))
