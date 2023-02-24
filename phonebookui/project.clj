(defproject todo-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.758"]
                 [thheller/shadow-cljs "2.12.5"]
                 [cljs-http "0.1.46"]
                 [uix/core "0.0.1-alpha"]
                 [uix/dom "0.0.1-alpha"]
                 [uix/rn "0.0.1-alpha"]]
  :target-path "target/%s"
  :source-paths ["src"]
  :resource-paths ["resources"]
  :profiles {:uberjar {:aot :all}})
