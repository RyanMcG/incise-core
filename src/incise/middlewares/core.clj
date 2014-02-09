(ns incise.middlewares.core
  (:require [taoensso.timbre :refer [trace spy]]
            [incise.load :refer [load-middlewares]]))

(defonce middlewares (atom {}))

(defn register* [rank middleware-var & more]
  (swap! middlewares assoc middleware-var {:rank rank
                                           :args more}))

(defmacro register [rank-or-middleware & more]
  (let [[rank middleware-sym more]
        (if (number? rank-or-middleware)
          [rank-or-middleware (first more) (rest more)]
          [600 rank-or-middleware more])]
  `(register* ~rank (var ~middleware-sym) ~@more)))

(defn- combine [app [middleware {:keys [args]}]]
  (apply middleware app args))

(defn wrap-app [app]
  (load-middlewares)
  (->> @middlewares
       (spy :trace)
       (sort-by (comp first val))
       (reduce combine app)))
