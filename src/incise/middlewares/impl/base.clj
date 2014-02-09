(ns incise.middlewares.impl.base
  (:require [incise.utils :refer [normalize-uri wrap-log-exceptions]]
            [taoensso.timbre :refer [color-str info]]
            (ring.middleware [reload :refer [wrap-reload]]
                             [stacktrace :refer [wrap-stacktrace-web]])
            [incise.middlewares.core :refer [register]]))

(def ^:private colors-by-code
  {2 :green
   3 :blue
   4 :yellow
   5 :red})

(defn- status->color [status]
  (colors-by-code (int (/ status 100)) :reset))

(defn- color-status [status]
  (color-str (status->color status)
             status))

(defn wrap-log-request [handler]
  (fn [{:keys [uri] :as request}]
    (let [uniq-id (gensym "REQUEST ")]
      (info (color-str :white uniq-id " START ┄┅┄ ~") uri)
      (let [{:keys [status] :as resp} (handler request)]
        (info (color-str :white uniq-id " END  ")
               (color-status status)
               (color-str :white \~) uri)
        resp))))

(defn wrap-static-index [handler]
  (fn [{:keys [uri] :as request}]
    (handler (assoc request :uri (normalize-uri uri)))))

(register 200 wrap-static-index)
(register 300 wrap-reload :dirs ["src"])
(register 700 wrap-log-request)
(register 800 wrap-log-exceptions)
(register 900 wrap-stacktrace-web)
