(ns incise.transformers.impl.html-skeleton-layout
  (:require (incise.transformers [core :refer [register]]
                                 [layout :refer [deflayout render-content]])))

(deflayout skeleton
  [{:keys [site-title]} {:keys [content]}]
  (str "<!DOCTYPE html>
       <html>
         <head>
           <title>" site-title "</title>
         </head>
         <body>" (render-content content) "</body>
       </html>"))

(register :html-skeleton-layout skeleton)
