(ns corbihttp.interceptor.handler
  (:require [bidi.bidi :as bidi]
            [corbihttp.metric :as metric]
            [corbihttp.log :as log]
            [corbihttp.params :as params]
            [exoscale.coax :as c]
            [exoscale.ex :as ex]))

(defn assert-spec-valid
  [spec params]
  (if spec
    (ex/assert-spec-valid spec params)
    params))

(defn dispatch-map->bidi
  [dispatch-map]
  ["/"
   (->> dispatch-map
        (map (fn [[handler config]]
               [(:path config) (if-let [method (:method config)]
                                 {method handler}
                                 handler)]))
        (into {}))])

(defn main-handler-enter
  [{:keys [request] :as ctx} registry dispatch-map handler-component not-found-handler router]
  (let [uri (str (:uri request))
        method (-> request :request-method name)
        request (bidi/match-route* router
                                   (:uri request)
                                   request)
        req-handler (:handler request)]
    ;; request is nil of no match
    (if-let [{:keys [handler-fn spec]} (get dispatch-map req-handler)]
      (metric/with-time
        registry
        :http.request.duration
        {"uri" uri
         "method"  method}
        (->> (handler-fn handler-component
                         (-> request
                             params/merge-params
                             (update :all-params
                                     #(->> (c/coerce spec (or % {}))
                                           (assert-spec-valid spec)))))
             (assoc ctx :response)))
      (do (log/warnf {}
                     "uri %s not found for method %s"
                     uri method)
          (assoc ctx :response (not-found-handler handler-component request))))))

(defn main-handler
  "This interceptor expects that the handler is in the :handler key"
  [registry dispatch-map handler-component not-found-handler]
  (let [router (dispatch-map->bidi dispatch-map)]
    {:name ::main-handler
     :enter (fn [ctx]
              (main-handler-enter ctx registry dispatch-map handler-component not-found-handler router))}))
