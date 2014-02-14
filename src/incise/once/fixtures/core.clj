(ns incise.once.fixtures.core
  (:require [incise.load :refer [load-once-fixtures]]
            [taoensso.timbre :refer [spy]]))

(defonce fixtures (atom {}))

(defn register
  "Register a fixture for once with the given rank. Rank should be a number. The
  higher the number the later the fixture is executed. (A fixture with rank 0
  would be the first thing to execute.)"
  [fixture & [rank]]
  (swap! fixtures assoc fixture (or rank 750)))

(defn- generate-thunk [thunk fixture]
  (fn [] (fixture thunk)))

(defn run-fixtures []
  (reset! fixtures {})
  (load-once-fixtures)
  ((->> @fixtures
        (spy :trace)
        (sort-by (comp - val))
        (map key)
        (reduce generate-thunk (fn [])))))
