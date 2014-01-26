(ns incise.layouts.impl.html-skeleton
  (:require (incise.layouts [core :refer [register]]
                            [utils :refer [deflayout
                                           render-content]])))

(deflayout skeleton
  [{:keys [site-title]} {:keys [content]}]
  (str "<!DOCTYPE html>
       <html>
         <head>
           <title>" site-title "</title>
         </head>
         <body>" (render-content content) "</body>
       </html>"))

(register :html-skeleton skeleton)
