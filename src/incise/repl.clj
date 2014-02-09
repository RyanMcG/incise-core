(ns incise.repl
  (:require [clojure.tools.namespace.repl :refer :all]
            [taoensso.timbre :as timbre]
            (incise [server :refer [stop start]]
                    [config :as conf]
                    [core :refer :all])))

(defn start-server [& {:as more}]
  (conf/load)
  (conf/merge! {:method :serve
                :port 5000
                :thread-count 4} more)
  (timbre/set-level! (conf/get :log-level))
  (timbre/merge-config! (conf/get :timbre))
  (start))

(defn restart-server
  "Stop a server if it is already started and start a new one."
  [& args]
  (stop)
  (require :reload 'incise.server)
  (apply start-server args))
