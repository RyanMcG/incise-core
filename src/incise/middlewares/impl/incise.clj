(ns incise.middlewares.impl.incise
  (:require [clojure.java.io :refer [file]]
            [clojure.set :refer [difference]]
            [ns-tracker.core :refer [ns-tracker]]
            (incise [utils :refer [log-file normalize-uri delete-recursively]]
                    [load :refer [load-parsers-and-layouts]]
                    [config :as conf])
            [incise.middlewares.core :refer [register]]
            (incise.parsers [core :refer [input-file-seq parse-all]]
                            [parse :refer [parses dissoc-parses!]])))

(defonce ^{:private true
           :doc "Keeps track of modification times of files for modified?."}
  file-modification-times
  (atom {}))

(defn- modified!?
  "If file is not in atom or it's modification date has advanced."
  [a-file]
  (let [previous-modification-time (@file-modification-times a-file)
        last-modification-time (.lastModified a-file)]
    (swap! file-modification-times assoc a-file (.lastModified a-file))
    (or (nil? previous-modification-time)
        (< previous-modification-time last-modification-time))))

(defn- reference-files!
  "Pass files through with side effects. Call dissoc-parses! on deleted paths."
  [paths-set files]
  (let [old-paths-set @paths-set
        new-paths-set (set (map (memfn getCanonicalPath) files))
        deleted-paths (difference old-paths-set new-paths-set)]
    (dissoc-parses! deleted-paths)
    (reset! paths-set new-paths-set))
  files)

(defn- output-path [a-file]
  "Look up the output path for the given input file in recorded parses."
  (:path (@parses (.getCanonicalPath a-file))))

(defn- requested-file?
  "A predicate which returns whether the given request is attempting to access
  the given file."
  [request a-file]
  (and (re-find #"html" ((request :headers) "accept"))
       (= (str \/ (output-path a-file))
          (normalize-uri (:uri request)))))

(defn- handle-generated-files
  [generated [source-file generated-files]]
  (let [previously-generated-files (@generated source-file)
        generated-files (set generated-files)
        files-to-delete (difference previously-generated-files generated-files)]
    (swap! generated assoc source-file generated-files)
    (doseq [file-to-delete files-to-delete]
      (.delete file-to-delete)
      (log-file :red "Deleted" file-to-delete))))

(defn wrap-incise-parse
  "Call parse on each modified file in the given dir with each request."
  [handler]
  (reset! file-modification-times {})
  (let [orig-out *out*
        orig-err *err*
        paths-set (atom #{})
        generated (atom {})]
    (delete-recursively (file (conf/get :out-dir)))
    (fn [request]
      (binding [*out* orig-out
                *err* orig-err]
        (->> (input-file-seq)
             (reference-files! paths-set)
             (filter (some-fn modified!? (partial requested-file? request)))
             (parse-all)
             (map (partial handle-generated-files generated))
             (dorun)))
      (handler request))))

(defn wrap-reset-modified-files-with-source-change
  "An almost copy of wrap-reload, but instead of reloading modified files this
   ensures that the next time parse is called all content files are re-parsed.

   Takes the following options:
     :dirs - A list of directories that contain the source files.
             Defaults to [\"src\"]."
  [handler & [options]]
  (let [source-dirs (:dirs options ["src"])
        modified-namespaces (ns-tracker source-dirs)]
    (fn [request]
      (when (seq (modified-namespaces)) (reset! file-modification-times {}))
      (handler request))))

(defn wrap-parsers-reload
  "Reload all parsers and layouts with each request."
  [handler]
  (fn [request]
    (load-parsers-and-layouts)
    (handler request)))

(register   0 wrap-incise-parse)
(register  50 wrap-reset-modified-files-with-source-change)
(register 100 wrap-parsers-reload)
