(ns incise.parsers.html-spec
  (:require [incise.parsers.html :refer :all]
            [incise.parsers.parse :refer [map->Parse]]
            [clj-time.coerce :refer [to-date]]
            [clojure.java.io :refer [file resource]]
            [speclj.core :refer :all]))

(describe "File->Parse"
  (with short-html-file (file (resource "spec/example.html")))
  (it "reads some stuff out of a file, yo"
    (should= (map->Parse {:title "example"
                          :layout :html-skeleton
                          :path "example/index.html"
                          :publish true
                          :content "\n\n<p>I am some html</p>\n"
                          :extension "/index.html"})
             (File->Parse identity @short-html-file))))

(run-specs)
