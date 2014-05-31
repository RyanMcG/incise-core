(ns incise.repl
  (:require [taoensso.timbre :as timbre]
            [com.stuartsierra.component :as component]
            (incise [server :refer [create-http-server]]
                    [config :as conf]
                    [core :refer :all])))

(defn- load-config! [config]
  (conf/load!)
  (conf/merge! {:method :serve} config))

(defn- setup-timbre! []
  (timbre/set-level! (conf/get :log-level))
  (timbre/merge-config! (conf/get :timbre)))

(defonce system (atom nil))

(defn init [& {:as config}]
  (load-config! config)
  (setup-timbre!)
  (swap! system (constantly (create-http-server))))

(defn- start []
  (swap! system component/start))

(defn- stop []
  (swap! system (fn [s] (if s (component/stop s)))))

(defn go [& more]
  (stop)
  (apply init more)
  (start))
