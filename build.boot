(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0" :scope "provided"]
                  [quil "2.6.0"]
                  [tolitius/boot-check "0.1.4" :scope "test"]
                  [funcool/boot-codeina "0.1.0-SNAPSHOT" :scope "test"]
                  [samestep/boot-refresh "0.1.0" :scope "test"]])

(require '[tolitius.boot-check :as check]
         '[funcool.boot-codeina :refer [apidoc]]
         '[samestep.boot-refresh :refer [refresh]])

(task-options!
  aot    {:all true}
  pom    {:project 'digger
          :version "0.0.0"
          :description "A clojure remake of Digger"}
  jar    {:main 'digger.core
          :file "digger.jar"}
  sift   {:include #{#"digger.jar"}}
  repl   {:init-ns 'digger.core}
  apidoc {:version "0.1.0"
          :title "Digger"
          :sources #{"src"}
          :description "1DEV Resit"})

(deftask build []
  (comp (aot) (pom) (uber) (jar) (sift) (target)))

(deftask check-sources []
  (set-env! :source-paths #{"src" "test"})
  (comp
   (check/with-yagni)
   (check/with-eastwood)
   (check/with-kibit)
   (check/with-bikeshed)))

;(deftask run []
;  (with-pass-thru _
;    (digger.core/-main)))
