(ns incise.once.fixtures.impl.incise
  (:require [incise.load :refer [load-parsers-and-layouts]]
            [incise.once.fixtures.core :refer [register]]
            [incise.parsers.core :refer [parse-all-input-files]]))

(defn- load-fixture [thunk]
  (load-parsers-and-layouts)
  (thunk))

(defn- parse-fixture [thunk]
  (thunk)
  (doall (parse-all-input-files)))

(register load-fixture -500)
(register parse-fixture 000)
