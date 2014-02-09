(ns incise.utils
  (:require [clojure.java.io :refer [file]]
            [taoensso.timbre :refer [error]])
  (:import [java.io File]))

(defn wrap-log-exceptions [func & {:keys [bubble] :or {bubble true}}]
  "Log (i.e. print) exceptions received from the given function."
  (fn [& args]
    (try
      (apply func args)
      (catch Throwable e
        (error e)
        (when bubble (throw e))))))

(defn slot-by
  "Take a function which when called on each item in the given collection
   returns a sequence of keys to value to."
  [keys-fn coll]
  (persistent! (reduce (fn [memo item]
                          (let [item-keys (keys-fn item)]
                            (doseq [a-key (if (sequential? item-keys)
                                            item-keys
                                            [item-keys])]
                              (assoc! memo
                                      a-key
                                      (conj (get memo a-key []) item))))
                          memo)
                        (transient {})
                        coll)))

(defn remove-prefix-from-path
  "Remove the given prefix from the given path."
  [prefix-file afile]
  (-> afile
      (file)
      (.getCanonicalPath)
      (subs (inc (count (.getCanonicalPath (file prefix-file)))))))

(defn normalize-uri
  "Prepend index.html to a uri with a trailing slash."
  [uri]
  (if (= (last uri) \/)
    (str uri "index.html")
    uri))

(defn directory? [^File afile] (.isDirectory afile))

(defn- gitignore-file? [^File file]
  (= (.getName file) ".gitignore"))

(defn delete-recursively
  "Delete a directory tree."
  [^File root]
  (when root
    (when (.isDirectory root)
      (doseq [file (remove gitignore-file? (.listFiles root))]
        (delete-recursively file)))
    (.delete root)))

(defn getenv
  "A nice wrapper around System/getenv that allows a second argument to be
  passed in as the default."
  [variable & [default]]
  (or (System/getenv variable) default))
