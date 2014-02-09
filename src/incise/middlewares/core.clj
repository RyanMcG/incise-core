(ns incise.middlewares.core
  (:require [incise.load :refer [load-middlewares]]))

(defonce middlewares (atom {}))

(defn register* [rank middleware-var & more]
  (swap! middlewares assoc middleware-var {:rank rank
                                           :args more}))

(defn- combine [app [middleware {:keys [args]}]]
  (apply middleware app args))

(defn wrap-app [app]
  (load-middlewares)
  (->> @middlewares
       (sort-by (comp first val))
       (reduce combine app)))
