(ns incise.core
  (:require (incise [config :as conf]
                    [cli :as cli]
                    [utils :refer [wrap-log-exceptions]]
                    [server :refer [create-servers]])
            [incise.once.core :refer [once]]
            [com.stuartsierra.component :as component]
            [incise.deployer.core :refer [deploy]]
            [chic-text.terminal :refer [terminal-table]]
            [taoensso.timbre :as timbre]
            [clojure.string :as s])
  (:gen-class))

(defn- exit [code & messages]
  (dorun (map println messages))
  (System/exit code))

(def ^:private normal-help-message
  "A tool for parsing and applying transformations on a set of files, dispatching by extension. Incise is very useful for generating static websites.")

(defn- with-args*
  "A helper function to with-args macro which does all the work.

  1.  Parse arguments
  2.  Load in the config
  3.  Merge some options into config
  4.  Handle help or call body-fn with options and cli arguments"
  [args body-fn]
  (let [cli-opts (cli/parse-args args)]
    (when-let [errors (seq (cli/errors cli-opts))]
      (exit 1
            (terminal-table errors)
            ""
            (cli/summary cli-opts)))

    (when (cli/help? cli-opts)
      (exit 0
            (terminal-table [normal-help-message])
            ""
            (cli/summary cli-opts)))

    (conf/load! (cli/config-path cli-opts))
    (let [options (cli/options cli-opts)]
      (conf/merge! options)
      (timbre/set-level! (conf/get :log-level))
      (timbre/merge-config! (conf/get :timbre))
      (body-fn options (cli/arguments cli-opts)))))

(defmacro ^:private with-args
  "Take arguments parsing them using cli and handle help accordingly."
  [args & body]
  `(with-args* ~args (fn [~'options ~'cli-args] ~@body)))

(defn- wrap-pre [func pre-func & more]
  (fn [& args]
    (apply pre-func more)
    (apply func args)))

(defn- wrap-post [func post-func & more]
  (fn [& args]
    (let [return-value (apply func args)]
      (apply post-func more)
      return-value)))

(defn- wrap-serve
  [main-func]
  (-> main-func
      (wrap-pre conf/avow)
      (wrap-log-exceptions :bubble false)))

(defn- wrap-main
  [main-func]
  (-> main-func
      (wrap-serve)
      (wrap-post #(System/exit 0))))

(defn- serve [] (component/start (create-servers)))
(defn -main
  "Based on the given args either deploy, compile or start the development
  server."
  [& args]
  (with-args args
    ((case (:method options)
       :deploy (wrap-main deploy)
       :once (wrap-main once)
       :serve (wrap-serve serve)))))
