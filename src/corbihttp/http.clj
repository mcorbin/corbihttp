(ns corbihttp.http
  (:require [com.stuartsierra.component :as component]
            [less.awful.ssl :as less-ssl]
            [ring.adapter.jetty :as jetty]))

(defrecord Server [config handler server]
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
                          :client-auth :need))]
      (assoc this :server
             (jetty/run-jetty handler
                              config))))
  (stop [this]
    (when server
      (.stop server))
    (assoc this :server nil)))
