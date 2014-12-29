(ns incise.cli-test
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [incise.cli :refer [parse-args summary options arguments help? errors]]))

;; Some helper functions for tests
(defn- submap? [super sub]
  {:pre [(map? super) (map? sub)]}
  (every? (fn [sub-key]
            (= (get super sub-key)
               (get sub sub-key)))
          (keys sub)))

(defn- str->args [str-args]
  (if (s/blank? str-args)
    []
    (s/split str-args #"\s")))

(defn- pargs [args] (-> args str->args parse-args))

(deftest test-parse-args
  (testing "arguments"
    (are [cli-args args] (= (arguments (pargs cli-args)) args)
         ""          []
         "something" ["something"]
         "any thing" ["any" "thing"]))
  (testing "help?"
    (are [cli-args help] (= (help? (pargs cli-args)) help)
         ""       false
         "-h"     true
         "--help" true
         "help"   true
         "h el p" false))
  (testing "options"
    (are [cli-args sub-opts] (submap? (options (pargs cli-args)) sub-opts)
         ""                 {:method :serve :port 5000}
         "-h"               {:help nil}
         "serve"            {:method :serve}
         "once"             {:method :once}
         "deploy"           {:method :deploy}
         "once serve"       {:method :once}
         "derp once"        {:method nil}
         "-g"               {:ignore-publish true}
         "-l debug"         {:log-level :debug}
         "--log-level info" {:log-level :info}
         "-u something"     {:uri-root "something"}
         "-i i -o o"        {:in-dir "i" :out-dir "o"}
         "--nrepl-port 100" {:nrepl-port 100}))
  (testing "errors"
    (are [cli-args errs] (= (errors (pargs cli-args)) errs)
         ""                   []
         "-h"                 []
         "help"               []
         "serve"              []
         "once -u derp"       []
         "deploy -t 8"        []
         "serve -L somewhere" ["Unknown option: \"-L\""]
         "serv"               ["The specified method (serv) is invalid. It must be either: deploy, help, once, serve."]))
  (testing "summary"
    (let [sumtext (summary (parse-args ["help"]))
          lines (set (s/split-lines sumtext))]
      (is (string? sumtext))
      (are [line] (contains? lines line)
           "Methods:"
           "Options:"))))
