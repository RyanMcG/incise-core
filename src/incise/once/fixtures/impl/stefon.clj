(ns incise.once.fixtures.impl.stefon
  (:require [incise.config :as conf]
            [incise.once.fixtures.core :refer [register]]
            [clojure.java.io :refer [file]]
            [taoensso.timbre :refer [info]]
            (stefon [settings :refer [with-options]]
                    [core :refer [precompile]])))

(defn stefon-fixture [thunk]
  (let [{:keys [out-dir stefon]} (conf/get)
        manifest-file-path (.getPath (file out-dir "manifest.json"))]
    (with-options (merge {:mode :production
                          :serving-root out-dir
                          :manifest-file manifest-file-path
                          :precompiles []} stefon)
      (info "Precompiling assets...")
      (precompile nil)
      (info "Done.")
      (thunk))))

(register stefon-fixture 400)
