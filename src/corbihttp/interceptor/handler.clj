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
   {:keys [handler-component]}]
  (let [{:keys [handler spec]} (:handler ctx)]
    (->> (handler handler-component
                  (-> request
                      params/merge-params
                      (update :all-params
                              #(->> (c/coerce spec (or % {}))
                                    (assert-spec-valid spec)))))
         (assoc ctx :response))))

(defn main-handler
  "This interceptor expects that the handler is in the :handler key."
  [params]
  {:name ::main-handler
   :enter (fn [ctx]
            (main-handler-enter ctx
                                params))})
