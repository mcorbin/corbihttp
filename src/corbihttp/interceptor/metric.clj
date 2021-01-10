(ns corbihttp.interceptor.metric
  (:require [corbihttp.metric :as metric]))

(defn response-metrics
  [registry]
  {:name ::response-metrics
   :leave (fn [ctx]
            (metric/http-response registry ctx)
            ctx)})

