(ns corbihttp.http
  (:require [com.stuartsierra.component :as component]
            [exoscale.interceptor :as interceptor]
            [ring.adapter.jetty :as jetty]))

(defn interceptor-handler
  [interceptor-chain]
  (fn handler [request]
    (interceptor/execute {:request request} interceptor-chain)))

(defrecord Server [config interceptor-chain server]
  component/Lifecycle
  (start [this]
    (assoc this :server
           (jetty/run-jetty (interceptor-handler interceptor-chain)
                            {:join? false
                             :host (:host config)
                             :port (:port config)})))
  (stop [this]
    (when server
      (.stop server))
    (assoc this :server nil)))
