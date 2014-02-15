(ns incise.test-helpers
  (:require [incise.config :as conf]))

(defmacro with-custom-config [custom-config & body]
  `(with-redefs [conf/config (atom (merge @conf/config ~custom-config))]
     ~@body))

(defn custom-config-fixturer [& {:as config}]
  (fn [f]
    (with-custom-config config
      (f))))
