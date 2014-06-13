(ns incise.once.fixtures.impl.incise
  (:require [incise.load :refer [load-parsers-and-transformers]]
            [incise.once.fixtures.core :refer [register]]
            [incise.parsers.core :refer [parse-all-input-files]]))

(defn- load-fixture [thunk]
  (load-parsers-and-transformers)
  (thunk))

(defn- parse-fixture [thunk]
  (thunk)
  (doall (parse-all-input-files)))

(register load-fixture -500)
(register parse-fixture   0)
