(ns incise.once.core-spec
  (:require [incise.once.core :refer [once]]
            [incise.utils :refer [temp-dir]]
            [clojure.java.io :refer [file]]
            [speclj.core :refer :all]))

(def spec-temp-dir (partial temp-dir "incise-once-spec"))

(describe "once"
  (with-all out-dir (spec-temp-dir))
  (with-all out-dir-path (.getCanonicalPath @out-dir))
  (with once-result (once :in-dir "resources/spec"
                          :out-dir @out-dir-path))
  (it "returns files parsed"
    (doseq [filename-title ["example"]]
      (should-contain (file @out-dir-path filename-title "index.html")
                      (reduce concat (vals @once-result))))))

(run-specs)
