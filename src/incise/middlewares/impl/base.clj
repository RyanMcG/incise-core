(ns incise.middlewares.impl.base
  (:require [incise.utils :refer [normalize-uri wrap-log-exceptions]]
            (ring.middleware [reload :refer [wrap-reload]]
                             [stacktrace :refer [wrap-stacktrace-web]])
            [incise.middlewares.core :refer [register]]))

(defn wrap-static-index [handler]
  (fn [{:keys [uri] :as request}]
    (handler (assoc request :uri (normalize-uri uri)))))

(register 200 wrap-static-index)
(register 300 wrap-reload :dirs ["src"])
(register 800 wrap-log-exceptions)
(register 900 wrap-stacktrace-web)
