(ns incise.parsers.html
  "Provides a function, html-parser, to create what could be considered the
   standard parser from a function which takes the body of a file as a string
   and returns html."
  (:require (incise.parsers [utils :refer [meta->write-path
                                           name-without-extension]]
                            [parse :refer [map->Parse
                                           record-parse!
                                           publish-parse?]])
            [incise.transformers.core :refer [transform]]
            [incise.config :as conf]
            [taoensso.timbre :refer [info]]
            [clojure.edn :as edn]
            [pallet.map-merge :refer [merge-keys]]
            [clj-time.coerce :refer [to-date]]
            [clojure.string :as s]
            [clojure.java.io :refer [file reader]])
  (:import [java.io File]
           [incise.parsers.parse Parse]))

(def default-meta {:transformers [:html-skeleton-layout
                                  :html-header-anchors]
                   :extension "/index.html"})

(defn- remove-trailing-index-html
  [path]
  (s/replace path #"/index\.html$" "/"))

(defn Parse->path [^Parse parse]
  (->> parse
       (:path)
       (remove-trailing-index-html)
       (str \/)))

(def ^:private root-title "index")
(def ^:private root-path "index.html")
(defn- root-title? [title] (-> title str s/lower-case (= root-title)))

(defn- meta-and-content->Parse
  "Combine the given meta data and content into a Parse."
  [{:keys [title date] :as parse-meta} content]
  (let [meta-with-defaults (merge-keys
                             {}
                             default-meta
                             (if (root-title? title) {:path root-path})
                             parse-meta)]
    (map->Parse
      (assoc meta-with-defaults
             :path (meta->write-path meta-with-defaults)
             :date (to-date date)
             :content content))))

(defn- starts-with-curly-brace? [s] (boolean (re-find #"^\s*\{" s)))
(defn- read-edn-map-from-beggining-of-string
  "Returns a map from the edn present at the beginning of the string if it
  starts with an edn map. Otherwise, return nil."
  [s]
  (if (starts-with-curly-brace? s)
    (try
      (edn/read-string s)
      (catch RuntimeException e
        (throw (ex-info (str "The given string seemed to start with a map, but "
                             "failed to be interpretted as edn.")
                        {:string s}
                        e))))))

(defn File->Parse
  "Read in a file and turn it into a Parse instance."
  [content-fn ^File file]
  (let [file-str (slurp file)
        parse-meta-from-file (read-edn-map-from-beggining-of-string file-str)
        parse-meta (merge (conf/get :parse-defaults)
                          {:title (name-without-extension file)}
                          parse-meta-from-file)
        content (content-fn (if parse-meta-from-file
                              (second (s/split file-str #"\}" 2))
                              file-str))]
    (meta-and-content->Parse parse-meta content)))

(defn write-Parse
  "Write the result of Parse->string to a file in the out-dir at the path
  specified in the given parse. Return the written File."
  [^Parse parse-data]
  (let [out-file (file (conf/get :out-dir) (:path parse-data))]
    (-> out-file
        (.getParentFile)
        (.mkdirs))
    (spit out-file (:content (transform (dissoc parse-data :path))))
    out-file))

(defn html-parser
  "Take a function that parses a string into HTML and returns a HTML parser.

  An HTML parser is a function which takes a file, reads it and writes it out
  as html to the proper place under public. If it is a page it should appear at
  the root, if it is a post it will be placed under a directory strucutre based
  on its date."
  [parser-fn]
  (fn html-parser-parser [file]
    (let [^Parse parse (File->Parse parser-fn file)]
      (when (publish-parse? parse)
        (record-parse! (.getCanonicalPath file) parse)
        (delay [(write-Parse parse)])))))
