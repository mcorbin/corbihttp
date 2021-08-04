(ns corbihttp.http
  (:require [com.stuartsierra.component :as component]
            [exoscale.interceptor :as interceptor]
            [less.awful.ssl :as less-ssl]
            [ring.adapter.jetty :as jetty]))

(defn handle!
  [chain]
  (fn [request]
    (interceptor/execute {:request request} chain)))

(defrecord Server [config chain-builder server chain]
  component/Lifecycle
  (start [this]
    (let [ssl-context (when (:cacert config)
                        (less-ssl/ssl-context (:key config)
                                              (:cert config)
                                              (:cacert config)))
          config (cond-> {:join? false
                          :host (:address config)}
                   (not ssl-context) (assoc :port (:port config))
                   ssl-context
                   (assoc :ssl? true
                          :http? false
                          :ssl-port (:port config)
                          :ssl-context ssl-context
                          :client-auth :need))
          chain (chain-builder this)]
      (assoc this
             :server (jetty/run-jetty (handle! chain)
                                      config)
             :chain chain)))
  (stop [this]
    (when server
      (.stop server))
    (assoc this :server nil :chain nil)))
