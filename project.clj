(defproject mcorbin/corbihttp "0.1.0-SNAPSHOT"
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
                 [exoscale/cloak "0.1.3"]
                 [exoscale/ex "0.3.16"]
                 [exoscale/interceptor "0.1.9"]
                 [io.micrometer/micrometer-registry-prometheus "1.6.1"]
                 [javax.xml.bind/jaxb-api "2.4.0-b180830.0359"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ring/ring-core "1.8.2"]
                 [ring/ring-jetty-adapter "1.8.2"]
                 [spootnik/signal "0.2.4"]
                 [spootnik/unilog "0.7.27"]]
  :repl-options {:init-ns corbihttp.core})
