(ns corbihttp.interceptor.params)

(def merge-params
  {:name ::merge-params
   :enter
   (fn [ctx]
     (let [request (:request ctx)]
       (assoc-in ctx
                 [:request :all-params]
                 (merge (:body request)
                        (:params request)))))})
