(ns incise.parsers.core-spec
  (:require [speclj.core :refer :all]
            [incise.config :as conf]
            (incise [load :refer [load-parsers-and-transformers]]
                    [spec-helpers :refer [redef-around]])
            [clojure.java.io :refer [file resource]]
            [incise.transformers.core :refer [transformers]]
            [incise.parsers.core :refer :all]))

(def redef-parsers (redef-around parsers (atom {})))
(def redef-transformers (redef-around transformers (atom {})))

(describe "register"
  redef-parsers
  (with parser (fn []))
  (it "can register parsers to extensions"
    (should-not-throw (register [:mkd "markdown"] @parser))
    (should-contain "markdown" @parsers)
    (should-contain "mkd" @parsers))
  (it "can register parsers to extensions"
    (should-not-throw (register ["markdown"] @parser)))
  (it "can register parser to extension"
    (should-not-throw (register "markdown" @parser)))
  (it "can register parsers to keyword extensions"
    (should-not-throw (register [:markdown] @parser))))

(describe "register-mappings!"
  redef-parsers
  (with parser (fn []))
  (with mappings {:markdown :mkd
                  :txt [:rst :thing]})
  (before (register :markdown @parser))
  (before (register :txt @parser))
  (it "register mappings copies parsers to new extensions"
    (register-mappings! @mappings)
    (doseq [parser-key (map name [:mkd :thing :rst])]
      (should-contain parser-key @parsers)
      (should= @parser (@parsers parser-key)))))

(describe "parsers"
  redef-parsers
  redef-transformers
  (it "is initially empty"
    (should (empty? @parsers)))
  (it "gets populted when parsers are loaded"
    (load-parsers-and-transformers)
    (doseq [extension ["htm" "html" "clj"]]
      (should-contain extension @parsers))))

(describe "parse"
  redef-parsers
  redef-transformers
  (before (load-parsers-and-transformers))
  (with real-html-file (file (resource "spec/example.html")))
  (with output-files ((parse @real-html-file)))
  (it "outputs html"
    (doseq [output-file @output-files]
      (should (.exists output-file))
      (should-contain #"<html>" (slurp output-file)))))

(run-specs)
