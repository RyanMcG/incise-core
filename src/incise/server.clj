(ns incise.server
  (:require (compojure [route :refer [files not-found]]
                       [core :refer [routes]])
            [incise.config :as conf]
            [incise.middlewares.core :as middlewares]
            [clojure.tools.nrepl.server :as nrepl]
            [taoensso.timbre :refer [info]]
            [org.httpkit.server :refer [run-server]]))

(def ^:private not-found-page
   "<!DOCTYPE html>
   <html>
     <head>
       <title>404 - Page not found</title>
     </head>
     <body>
       <h1>404</h1>
       <h2>Page not found</h2>
     </body>
   </html>")

(defn create-handler []
  (routes (files "/" {:root (conf/get :out-dir)})
          (not-found not-found-page)))

(defn create-app
  "Create a ring application that is a deverlopment friendly server."
  []
  (middlewares/wrap-app (create-handler)))

(defn serve
  "Start a development server."
  []
  (let [port (conf/get :port)]
    (info "Serving at" (str "http://localhost:" port \/))
    (run-server (create-app) {:port port
                              :thread (conf/get :thread-count)})))

;; ## Functions for manipulating the server atom.
(defonce server (atom nil))
(defonce nrepl-server (atom nil))

(defn serve-nrepl []
  (let [nrepl-config (merge {:port (inc (conf/get :port 5000))
                             :bind "127.0.0.1"}
                            (conf/get :nrepl))
        {:keys [port ss] :as server} (apply nrepl/start-server
                                            (flatten (seq nrepl-config)))]
    (info "Started nrepl server at" (str (-> ss
                                             (.getInetAddress)
                                             (.getCanonicalHostName)) \: port))
    (spit ".nrepl-port" port)
    server))

(defn start
  "Start a ring server and an nrepl server and bind the results to the server
  and nrepl-server-atoms atom."
  [& args]
  (reset! nrepl-server (apply serve-nrepl args))
  (reset! server (apply serve args)))

(defn stop []
  (when @server
    (@server)
    (reset! server nil)
    (nrepl/stop-server @nrepl-server)
    (reset! nrepl-server nil)))
