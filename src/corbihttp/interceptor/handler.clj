(ns corbihttp.interceptor.handler
  (:require [corbihttp.params :as params]
            [exoscale.coax :as c]
            [exoscale.ex :as ex]))

(defn assert-spec-valid
  [spec params]
  (if spec
    (ex/assert-spec-valid spec params)
    params))

(defn main-handler-enter
  [{:keys [request] :as ctx}
   {:keys [dispatch-map
           handler-component]}]
  (let [req-handler (:handler ctx)
        {:keys [handler-fn spec]} (get dispatch-map req-handler)]
    (->> (handler-fn handler-component
                     (-> request
                         params/merge-params
                         (update :all-params
                                 #(->> (c/coerce spec (or % {}))
                                       (assert-spec-valid spec)))))
         (assoc ctx :response))))

(defn main-handler
  "This interceptor expects that the handler is in the :handler key."
  [{:keys [extra-handler] :as params}]
  (let [extra-handler (or extra-handler identity)]
    {:name ::main-handler
     :enter (fn [ctx]
              (main-handler-enter ctx
                                  (assoc params
                                         :extra-handler extra-handler)))}))
