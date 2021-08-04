(ns corbihttp.http-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [corbihttp.http :as http]
            [exoscale.telex :as c]))

(defn handler
  [_]
  {:status 200
   :body "foo"})

(defn builder
  [_]
  [{::enter (fn [_] {:status 200
                     :body "ok"})}])

(deftest server-test
  (let [server (component/start (http/map->Server {:config {:host "127.0.0.1"
                                                            :port 0}
                                                   :chain-builder builder}))
        port (.getLocalPort (first (.getConnectors (:server server))))
        client (c/client {:exoscale.telex.client/connect-timeout 2000})
        response @(c/request client {:method :get
                                     :url (str "http://127.0.0.1:" port)})]
    (is (= (:ring1.response/status response) 200))
    (component/stop server)))
