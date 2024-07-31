(ns corbihttp.interceptor.route
  (:require [corbihttp.metric :as metric]
            [corbihttp.log :as log]
            [exoscale.ex :as ex]
            [reitit.core :as r])
  (:import io.micrometer.core.instrument.Timer))

(defn route!
  [{:keys [request] :as ctx}
   {:keys [registry router]}]
  (let [uri (str (:uri request))
        method (-> request :request-method)
        match-result (r/match-by-path router
                                      uri)
        req-handler (get (:data match-result) method)]
    (if req-handler
      (assoc ctx
             :request (assoc request :path-params (:path-params match-result))
             :start-time (when registry
                           (java.time.Instant/now))
             :handler req-handler
             :timer (when registry
                      (metric/get-timer! registry
                                         :http.requests.duration
                                         {"uri" uri
                                          "method" (name method)})))
      (do (log/warnf {}
            "uri %s not found for method %s"
            uri method)
          (throw (ex/ex-info (format "uri %s not found for method %s"
                                     uri (name method))
                             [::not-found [:corbi/user ::ex/not-found]]))))))

(defn route
  "Computes the handler to use using the provided router"
  [{:keys [router] :as params}]
  (let [final-router (r/router router)]
    {:name ::main-handler
     :enter (fn [ctx]
              (route! ctx
                      (assoc params :router final-router)))
     :leave (fn [ctx]
              (when-let [start-time (:start-time ctx)]
                (let [end (java.time.Instant/now)]
                  (.record ^Timer (:timer ctx)
                           (java.time.Duration/between start-time end)))
                ctx))}))
