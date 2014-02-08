(ns incise.once.core
  (:require (incise [config :as conf]
                    [utils :refer [delete-recursively]])
            [incise.once.fixtures.core :refer [run-fixtures]]
            [clojure.java.io :refer [file]]
            [taoensso.timbre :refer [info]]))

(defn clear-out-dir []
  (let [out-dir (conf/get :out-dir)]
    (info "Clearing out" (str \" out-dir \"))
    (delete-recursively (file out-dir))))

(defn once
  "Incise just once. This requires that config is already loaded."
  [& {:as config}]
  (conf/merge! config)
  (conf/avow!)
  (clear-out-dir)
  (run-fixtures))
