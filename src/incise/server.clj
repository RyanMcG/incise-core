(ns incise.server
  (:require (compojure [route :refer [files not-found]]
                       [core :refer [routes]])
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [incise.config :as conf]
            [incise.middlewares.core :as middlewares]
            [clojure.tools.nrepl.server :as nrepl]
            [taoensso.timbre :refer [report info]]
            [org.httpkit.server :refer [run-server]]))

(def ^:private not-found-page
   "<!DOCTYPE html>
   <html>
     <head>
       <title>404 - Page not found</title>
       <style type=\"text/css\">
         h1 {
           font-weight: 100;
           font-size: 3em;
         }
         h2 {
           font-weight: 500;
           font-size: 2em;
         }
         html {
           height: 100%;
           background-color: #ddd;
         }
         body {
           font-family: sans-serif;
           padding: 5% 10% 0;
         }
       </style>
     </head>
     <body>
       <h1>404</h1>
       <h2>Page not found</h2>
     </body>
   </html>")

(defn- create-handler []
  (routes (files "/" {:root (conf/get :out-dir)})
          (not-found not-found-page)))

(defn- create-app
  "Create a ring application that is a deverlopment friendly server."
  []
  (middlewares/wrap-app (create-handler)))

(defn- serve
  "Start a development server."
  []
  (let [port (conf/get :port)]
    (report "Serving server at" (str "http://localhost:" port \/))
    (run-server (create-app) {:port port
                              :thread (conf/get :thread-count)})))

(defn- get-nrepl-port-file [] (io/file ".nrepl-port"))
(defn- serve-nrepl [port]
  (let [{:keys [port] :as server} (nrepl/start-server
                                       :port (conf/get :nrepl-port 0))]
    (info "Started nrepl on port " port)
    (spit (get-nrepl-port-file) port)
    server))

(defrecord NreplServer [port server]
  component/Lifecycle
  (start [this]
    (assoc this :server (serve-nrepl port)))
  (stop [this]
    (nrepl/stop-server server)
    (.delete (get-nrepl-port-file))
    (info "Stopped nREPL server on port" port)
    (assoc this :server nil)))

(defrecord HttpServer [port stop-server]
  component/Lifecycle
  (start [this] (assoc this :stop-server (serve)))
  (stop [this]
    (stop-server)
    (report "Stopped HTTP server on port" port)
    (assoc this :stop-server nil)))

(defn create-http-server []
  (map->HttpServer {:port (conf/get :port)}))

(defn create-nrepl-server []
  (map->NreplServer {:port (conf/get :nrepl-port)}))

(defn create-servers []
  (component/system-map
    :http (create-http-server)
    :nrepl (create-nrepl-server)))
