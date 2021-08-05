(ns corbihttp.interceptor.route
  (:require [bidi.bidi :as bidi]
            [corbihttp.metric :as metric]
            [corbihttp.log :as log]
            [exoscale.ex :as ex]
            [exoscale.interceptor :as itc])
  (:import io.micrometer.core.instrument.Timer))

(defn dispatch-map->bidi
  [dispatch-map]
  ["/"
   (->> dispatch-map
        (map (fn [[handler config]]
               [(:path config) (if-let [method (:method config)]
                                 {method handler}
                                 handler)]))
        (into {}))])

(defn route!
  [{:keys [request] :as ctx}
   {:keys [dispatch-map registry router handler-component not-found-handler]}]
  (let [uri (str (:uri request))
        method (-> request :request-method name)
        request (bidi/match-route* router
                                   (:uri request)
                                   request)
        req-handler (:handler request)]
    (if (get dispatch-map req-handler)
      (assoc ctx
             :request request
             :start-time (when registry
                           (java.time.Instant/now))
             :handler req-handler
             :timer (when registry
                      (metric/get-timer! registry
                                         :http.request.duration
                                         {"uri" uri
                                          "method"  method})))
      (do (log/warnf {}
                     "uri %s not found for method %s"
                     uri method)
          ;; TODO: why terminate is not working ?
          (comment
            (itc/terminate (assoc ctx :response
                                  (not-found-handler handler-component request))))
          (throw (ex/ex-info (format "uri %s not found for method %s"
                                     uri method)
                             [::not-found [:corbi/user ::ex/not-found]]))))))

(defn route
  "This set the :handler request value based on the request and the dispatch map."
  [{:keys [dispatch-map] :as params}]
  (let [router (dispatch-map->bidi dispatch-map)]
    {:name ::main-handler
     :enter (fn [ctx]
              (route! ctx
                      (assoc params
                             :router router)))
     :leave (fn [ctx]
              (when-let [start-time (:start-time ctx)]
                (let [end (java.time.Instant/now)]
                  (.record ^Timer (:timer ctx)
                           (java.time.Duration/between start-time end)))
                ctx))}))
