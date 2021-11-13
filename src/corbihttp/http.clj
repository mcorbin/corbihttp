(ns corbihttp.http
  (:require [clojure.spec.alpha :as s]
            [com.stuartsierra.component :as component]
            [corbihttp.spec :as spec]
            [exoscale.interceptor :as interceptor]
            [less.awful.ssl :as less-ssl]
            [ring.adapter.jetty :as jetty]))

(s/def ::host ::spec/ne-string)
(s/def ::port ::spec/port)

(s/def ::cacert ::spec/file-spec)
(s/def ::cert ::spec/file-spec)
(s/def ::key ::spec/file-spec)
(s/def ::username ::spec/ne-string)
(s/def ::password ::spec/secret)
(s/def ::basic-auth (s/keys :req-un [::username ::password]))

(s/def ::http (s/keys :req-un [::host ::port]
                      :opt-un [::cacert ::cert ::key ::basic-auth]))

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
                          :host (:host config)}
                   (not ssl-context) (assoc :port (:port config))
                   ssl-context
                   (assoc :ssl? true
                          :http? false
                          :ssl-port (:port config)
                          :ssl-context ssl-context
                          :client-auth :need))
          chain (->> (chain-builder this)
                     (remove nil?))]
      (assoc this
             :server (jetty/run-jetty (handle! chain)
                                      config)
             :chain chain)))
  (stop [this]
    (when server
      (.stop ^org.eclipse.jetty.server.Server server))
    (assoc this :server nil :chain nil)))
