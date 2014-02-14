(ns incise.once.fixtures.core
  (:require (incise [load :refer [load-once-fixtures load-parsers-and-layouts]]
                    [config :as conf])
            [clojure.java.io :refer [file]]
            [taoensso.timbre :refer [spy]]
            [incise.parsers.core :refer [parse-all-input-files]]))

(defn- load-fixture [thunk]
  (load-once-fixtures)
  (load-parsers-and-layouts)
  (thunk))

(defn- parse-fixture [thunk]
  (thunk)
  (doall (parse-all-input-files)))

(def fixtures (atom {load-fixture 500
                     parse-fixture 1000}))

(defn register
  "Register a fixture for once with the given rank. Rank should be a number. The
  higher the number the later the fixture is executed. (A fixture with rank 0
  would be the first thing to execute.)"
  [fixture & [rank]]
  (swap! fixtures assoc fixture (or rank 750)))

(defn- generate-thunk [thunk fixture]
  (fn [] (fixture thunk)))

(defn run-fixtures []
  ((->> @fixtures
        (spy :trace)
        (sort-by (comp - val))
        (map key)
        (reduce generate-thunk (fn [])))))
