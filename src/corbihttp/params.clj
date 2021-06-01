(ns corbihttp.params)

(defn merge-params
  [request]
  (assoc request
         :all-params
         (merge (:body request)
                (:params request)
                (:route-params request))))
