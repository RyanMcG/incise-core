(ns incise.middlewares.core
  (:require [incise.load :refer [load-middlewares]]))

(defonce middlewares (atom {}))

(defn register [rank-or-middleware & more]
  (let [[rank middleware args] (if (fn? rank-or-middleware)
                                 [600 rank-or-middleware more]
                                 [rank-or-middleware (first more) (rest more)])]
    (swap! middlewares assoc middleware [rank args])))

(defn- combine [app [middleware [_ args]]]
  (apply middleware app args))

(defn wrap-app [app]
  (load-middlewares)
  (->> @middlewares
       (sort-by (comp first val))
       (reduce combine app)))
