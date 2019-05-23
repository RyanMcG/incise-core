(ns incise.transformers.impl.html-header-anchors-spec
  (:require [incise.transformers.impl.html-header-anchors :refer
             [prefix-headers-with-anchors html-header-anchors]]
            [speclj.core :refer [describe it with should=]]))

(describe 'prefix-headers-with-anchors
  (with sample-source (str "<html><head><title>Yay</title></head><body>"
                           "<h1>ONE</h1>"
                           "<h2>Two</h2>"
                           "<h1>Another One</h1>"
                           "<h3>three</h3>"
                           "<h4>four</h4>"
                           "<h5>five</h5>"
                           "<h6>six <span>with a span</span></h6>"
                           "</body></html>"))
  (it "should put in the proper anchors"
    (should= (prefix-headers-with-anchors @sample-source)
             "<html><head><title>Yay</title></head><body><h1><a class=\"incise-generated-tag\" href=\"#one\" name=\"one\"></a>ONE</h1><h2><a class=\"incise-generated-tag\" href=\"#two\" name=\"two\"></a>Two</h2><h1><a class=\"incise-generated-tag\" href=\"#another-one\" name=\"another-one\"></a>Another One</h1><h3><a class=\"incise-generated-tag\" href=\"#three\" name=\"three\"></a>three</h3><h4><a class=\"incise-generated-tag\" href=\"#four\" name=\"four\"></a>four</h4><h5><a class=\"incise-generated-tag\" href=\"#five\" name=\"five\"></a>five</h5><h6><a class=\"incise-generated-tag\" href=\"#six-with-a-span\" name=\"six-with-a-span\"></a>six <span>with a span</span></h6></body></html>")))

(describe 'html-header-anchors
  (with html-source-str "<html><head><title>Yay</title></head><body><h4>Yep</h4></body></html>")
  (with data {:another-key 1 :content @html-source-str})
  (it "should add anchors to content in data"
    (should= {:another-key 1
              :content  "<html><head><title>Yay</title></head><body><h4><a class=\"incise-generated-tag\" href=\"#yep\" name=\"yep\"></a>Yep</h4></body></html>"}
             (html-header-anchors @data))))

(run-specs)
