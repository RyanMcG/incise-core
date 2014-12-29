(ns incise.cli
  (:require (incise [utils :refer [getenv]]
                    [config :refer [default-config]])
            [clojure.string :as s]
            [chic-text.terminal :refer [terminal-table-of]]
            [clojure.tools.cli :refer [parse-opts]]))

(defprotocol CliOptions
  (arguments [this])
  (config-path [this])
  (errors [this])
  (help? [this])
  (summary [this])
  (options [this]))

(def ^:private cli-options
  [["-h" "--help" "Print this help."]
   ["-c" "--config CONFIG_FILE"
    "The path to an edn file acting as configuration for incise"]
   ["-p" "--port PORT" "The port number to run the development server on."
    :default (getenv "INCISE_PORT" (default-config :port))
    :parse-fn #(Integer. %)
    :validate [#(< 0 % 0x10000)
               "Must be a number between 0 and 65536"]]
   [nil "--nrepl-port PORT" "The port number to run the nrepl server on."
    :default (getenv "INCISE_NREPL_PORT" (default-config :nrepl-port))
    :parse-fn #(Integer. %)
    :validate [#(< 0 % 0x10000)
               "Must be a number between 0 and 65536"]]
   ["-t" "--thread-count THREAD_COUNT"
    "The number of threads for the development server to use."
    :default (getenv "INCISE_THREAD_COUNT" (default-config :thread-count))
    :parse-fn #(Integer. %)]
   ["-g" "--ignore-publish"
    "Ignore the publish config for content (i.e. parse regardless)."]
   ["-l" "--log-level LOG_LEVEL" "At what level to log at"
    :parse-fn keyword]
   ["-i" "--in-dir INPUT_DIRECTORY" "The directory to get source from"]
   ["-o" "--out-dir OUTPUT_DIRECTORY" "The directory to put content into"]
   ["-u" "--uri-root URI_ROOT"
    "The path relative to the domain root where the generated site will be hosted."]])

(def ^:private cli-methods
  {:serve   "Start a webserver for development (the default)."
   :once    "Generate a website into the configured output directory."
   :deploy  "Uses once to generate output and then deploys it using the configured deployer."
   :help    "Print this help."})

(def ^:private valid-methods (set (keys cli-methods)))

(defn- method [cli-opts]
  (valid-methods (keyword (or (first (arguments cli-opts)) "serve"))))

(defn generate-methods-desc []
  (terminal-table-of cli-methods "  " (comp name key) " – " val))

(defrecord ToolsCliOptions [options arguments summary errors]
  CliOptions
  (arguments [this] (:arguments this))
  (summary [this] (str "Methods:\n"
                       (generate-methods-desc)
                       "\n\nOptions:\n"
                       (:summary this)))
  (errors [this]
    (sequence (if (method this)
                (:errors this)
                (conj (:errors this)
                      (str "The specified method ("
                           (first arguments)
                           ") is invalid. It must be either: "
                           (s/join ", " (sort (map name valid-methods))) \.)))))
  (config-path [this] (:config (:options this)))
  (options [this] (dissoc (assoc (:options this)
                                 :method (method this))
                          :help :config))
  (help? [this] (boolean (or (= (method this) :help)
                             (:help (:options this))))))

(defn- create-summary [specs]
  (terminal-table-of specs
                     "  "
                     (fn [{:keys [short-opt long-opt required]}]
                       (str (if short-opt (str short-opt ", "))
                            long-opt
                            (if required (str " " required))))
                     " – "
                     (fn [{:keys [desc validate-msg default]}]
                       (str desc
                            (if validate-msg (str " " validate-msg))
                            (if default (str " [default: " default \]))))))
(defn parse-args [args]
  (map->ToolsCliOptions (parse-opts args cli-options
                                    :summary-fn create-summary)))
