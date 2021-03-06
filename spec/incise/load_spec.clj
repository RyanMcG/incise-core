(ns incise.load-spec
    (:require [speclj.core :refer :all]
              [incise.load :refer :all]))

(defn it-should-load-expected-namespaces [expected-namespaces loader]
  (it "loads only the apporiate namespaces"
    (should= (set expected-namespaces) (set (loader)))))

(describe "loading"
  (with ns-syms '[incise.transformers.impl.cool
                  incise.transformers.impl.cool-test
                  incise.parsers.impl.cool
                  incise.parsers.impl.cool-spec
                  incise.deployer.impl.cool
                  incise.once.fixtures.impl.cool])
  (around [it]
    (with-redefs [incise.load/require-sym identity
                  incise.load/get-namespaces-from-classpath (fn [] @ns-syms)]
      (it)))
  (around [it] (with-out-str (it)))
  (context "deployers"
    (it-should-load-expected-namespaces '[incise.deployer.impl.cool]
                                        load-deployers))
  (context "transformers and parsers"
    (it-should-load-expected-namespaces '[incise.parsers.impl.cool
                                          incise.transformers.impl.cool]
                                        load-parsers-and-transformers))
  (context "once fixtures"
    (it-should-load-expected-namespaces '[incise.once.fixtures.impl.cool]
                                        load-once-fixtures)))

(run-specs)
