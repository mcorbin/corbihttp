(defproject fr.mcorbin/corbihttp "0.35.0"
  :description "Shared namespaces for my http projects"
  :url "https://github.com/mcorbin/corbihttp"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [byte-streams "0.2.4"]
                 [cheshire "5.13.0"]
                 [com.stuartsierra/component "1.1.0"]
                 [commons-codec/commons-codec "1.16.1"]
                 [commons-validator/commons-validator "1.7"]
                 [environ "1.2.0"]
                 [exoscale/cloak "0.1.10"]
                 [exoscale/coax "2.0.0"]
                 [exoscale/ex "0.4.1"]
                 [exoscale/interceptor "0.1.16"]
                 [exoscale/specs "1.0.6"]
                 [io.micrometer/micrometer-registry-prometheus "1.13.2"]
                 [javax.xml.bind/jaxb-api "2.4.0-b180830.0359"]
                 [less-awful-ssl "1.0.6"]
                 [metosin/reitit-core "0.6.0"]
                 [org.clojure/clojure "1.11.2"]
                 [org.clojure/tools.logging "1.3.0"]
                 [spootnik/constance "0.5.4"]
                 [ring/ring-core "1.12.1"]
                 [ring/ring-headers "0.4.0"]
                 [info.sunng/ring-jetty9-adapter "0.33.0"]
                 [spootnik/signal "0.2.5"]
                 [spootnik/unilog "0.7.31"]]
  :repl-options {:init-ns corbihttp.core}
  :global-vars {*warn-on-reflection* true}
  :profiles {:dev {:dependencies [[exoscale/telex "0.1.7"]
                                  [pjstadig/humane-test-output "0.11.0"]]
                   :plugins [[lein-ancient "0.6.15"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}})
