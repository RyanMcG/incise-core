(ns incise.deployer.core
  (:require (incise [config :as conf]
                    [load :refer [load-deployers]]))
  (:refer-clojure :exclude [get]))

(defonce workflows (atom {}))

(defn get [& args]
  (apply clojure.core/get @workflows args))

(defn deploy
  "Deploy using the user's specified workflow."
  [& {:as config}]
  (conf/merge! config)
  (load-deployers)
  (let [{workflow-name :workflow :as settings} (conf/get :deploy)
        workflow (get workflow-name)]
    (if workflow
      (workflow settings)
      (throw (RuntimeException. (str "No workflow registered as " workflow-name))))))

(defn register
  "Register a deployer."
  [workflow-name workflow]
  (swap! workflows
         assoc workflow-name workflow))
