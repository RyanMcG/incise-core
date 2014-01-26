(ns incise.spec-helpers
  "A namespace for defining helpers which are used in multiple spec namespaces."
  (:require [incise.test-helpers :refer :all]
            [speclj.core :refer [around]]))

(defmacro around-with-custom-config [& {:as custom-config}]
  `(around [it#] (with-custom-config ~custom-config (it#))))
