(defproject mcorbin/corbihttp "0.14.0"
  :description "Shared namespaces for my http projects"
  :url "https://github.com/mcorbin/corbihttp"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [bidi "2.1.6"]
                 [byte-streams "0.2.4"]
                 [cheshire "5.10.0"]
                 [com.stuartsierra/component "1.0.0"]
                 [commons-codec/commons-codec "1.15"]
                 [environ "1.2.0"]
                 [exoscale/cloak "0.1.6"]
                 [exoscale/coax "1.0.0-alpha12"]
                 [exoscale/ex "0.3.17"]
                 [exoscale/interceptor "0.1.9"]
                 [io.micrometer/micrometer-registry-prometheus "1.6.2"]
                 [javax.xml.bind/jaxb-api "2.4.0-b180830.0359"]
                 [less-awful-ssl "1.0.6"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ring/ring-core "1.8.2"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [spootnik/signal "0.2.4"]
                 [spootnik/unilog "0.7.27"]]
  :repl-options {:init-ns corbihttp.core}
  :global-vars {*warn-on-reflection* true}
  :profiles {:dev {:dependencies [[exoscale/telex "0.1.1"]
                                  [pjstadig/humane-test-output "0.10.0"]]
                   :plugins [[lein-ancient "0.6.15"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}})
