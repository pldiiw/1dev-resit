(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                  [quil "2.6.0"]])

(task-options!
  aot {:all true}
  pom {:project 'digger
       :version "0.0.0"
       :description "A clojure remake of Digger"}
  jar {:main 'digger.core
       :file "digger.jar"}
  sift {:include #{#"digger.jar"}}
  repl {:init-ns 'digger.core})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (sift) (target)))

(require 'digger.core)
(deftask run []
  (with-pass-thru _
    (digger.core/-main)))
