(ns incise.test-helpers)

(defmacro with-custom-config [custom-config & body]
  `(with-redefs [incise.config/config
                 (atom (merge @incise.config/config
                              ~custom-config))]
     ~@body))

