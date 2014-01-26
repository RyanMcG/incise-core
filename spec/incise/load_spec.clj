(ns incise.load-spec
    (:require [speclj.core :refer :all]
              [incise.load :refer :all]))

(describe "namespace is layout or parser filter"
  (it "returns false on non-parser and non-layout namespaces"
    (doseq [ns-sym ['arbitrary.name.space 'my.parser.yay 'your.layout.boop]]
      (should-not (#'incise.load/namespace-is-layout-or-parser? ns-sym))))
  (it "returns true for parser and layout namespaces"
    (doseq [ns-sym ['incise.parsers.impl.cool-thing
                    'incise.layouts.impl.bootstrapped]]
      (should (#'incise.load/namespace-is-layout-or-parser? ns-sym)))))

(describe "namespace is spec or test"
  (it "returns true for specs and tests"
    (doseq [ns-sym ['cool.thing-test 'incise.parsers.core-spec]]
      (should (#'incise.load/namespace-is-spec-or-test? ns-sym))))
  (it "returns false for other namespaces"
    (doseq [ns-sym ['cool.thing 'incise.parsers.batman]]
      (should-not (#'incise.load/namespace-is-spec-or-test? ns-sym)))))

(describe "finding parsers and layouts"
  (with layout-and-parser-syms (#'incise.load/filter-namespaces
                                 #'incise.load/namespace-is-layout-or-parser?))
  (it "does not contain spec namespaces"
    (doseq [spec-sym ['incise.parsers.impl.copy-spec]]
      (should-not-contain spec-sym @layout-and-parser-syms)))
  (it "contains default layouts and parsers"
    (doseq [default-sym ['incise.parsers.impl.html
                         'incise.layouts.impl.html-skeleton]]
      (should-contain default-sym @layout-and-parser-syms))))

(run-specs)
