(ns incise.middlewares.impl.stefon
  (:require [incise.config :as conf]
            [incise.middlewares.core :refer [register]]
            [stefon.core :refer [asset-pipeline]]))

(register 1000 asset-pipeline (conf/get :stefon))
