(defproject fr.mcorbin/corbihttp "0.25.0"
  :description "Shared namespaces for my http projects"
  :url "https://github.com/mcorbin/corbihttp"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[aero "1.1.6"]
                 [byte-streams "0.2.4"]
                 [cheshire "5.10.1"]
                 [com.stuartsierra/component "1.0.0"]
                 [commons-codec/commons-codec "1.15"]
                 [commons-validator/commons-validator "1.7"]
                 [environ "1.2.0"]
                 [exoscale/cloak "0.1.8"]
                 [exoscale/coax "1.0.0-alpha12"]
                 [exoscale/ex "0.3.18"]
                 [exoscale/interceptor "0.1.9"]
                 [exoscale/specs "1.0.0-alpha13"]
                 [io.micrometer/micrometer-registry-prometheus "1.8.1"]
                 [javax.xml.bind/jaxb-api "2.4.0-b180830.0359"]
                 [less-awful-ssl "1.0.6"]
                 [metosin/reitit-core "0.5.15"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.logging "1.2.3"]
                 [spootnik/constance "0.5.4"]
                 [ring/ring-core "1.9.4"]
                 [ring/ring-jetty-adapter "1.9.4"]
                 [spootnik/signal "0.2.4"]
                 [spootnik/unilog "0.7.29"]]
  :repl-options {:init-ns corbihttp.core}
  :global-vars {*warn-on-reflection* true}
  :profiles {:dev {:dependencies [[exoscale/telex "0.1.6"]
                                  [pjstadig/humane-test-output "0.11.0"]]
                   :plugins [[lein-ancient "0.6.15"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]}})
