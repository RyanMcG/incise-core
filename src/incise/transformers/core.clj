(ns incise.transformers.core
  (:require [incise.config :as conf]
            [clojure.set :refer [difference]]
            [manners.victorian :refer [as-coach avow]]
            [taoensso.timbre :refer [spy]])
  (:refer-clojure :exclude [get]))

(defonce transformers (atom {}))

(defn exists?
  "Check for the existance of a transformer with the given name."
  [transformer-with-name]
  (contains? @transformers (name transformer-with-name)))

(defn get [transformer-name & more]
  (apply @transformers (name transformer-name) more))

(defn- get-transfomer-keys [{tkeys :transformers :as data}]
  (if-let [tkeys ((if (sequential? tkeys) seq list) tkeys)]
    (map name tkeys)
    (throw (ex-info "No transformers specified in given parse."
                    {:available-transformers @transformers
                     :parse data}))))

(defn- dissoc-untranformable-keys [data]
  (dissoc data :transformers))

(def ^:private transformer-keys-coach
  (as-coach (fn [tkeys] (difference (set tkeys) (set (keys @transformers))))))

(defn lookup-transformers [tkeys]
  (avow (str "specified transformers (" tkeys
             "), the following do not match registered transformers")
        transformer-keys-coach tkeys)
  (map @transformers tkeys))

(defn- invoke-transformer [parse-data transformer] (transformer parse-data))
(defn transform
  "Take a Parse or parse-like map and pass it through the appropriate transformer."
  [data]
  (let [tkeys (get-transfomer-keys data)
        transformable-data (dissoc-untranformable-keys data)]
    (->> tkeys
         (lookup-transformers)
         (reduce invoke-transformer transformable-data))))

(defn register
  "Register a transformer function to a shortname.

  A transformer function takes one argument which is a pares-like map. The
  return value is passed to the next transformer in the sequence."
  [short-names transformer-fn]
  {:pre [(fn? transformer-fn)]}
  (swap! transformers
         merge (zipmap (map name (if (sequential? short-names)
                                   short-names
                                   [short-names]))
                       (repeat transformer-fn))))
