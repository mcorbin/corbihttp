(ns corbihttp.params
  (:require [corbihttp.interceptor.json :as json]))

(defn merge-params
  [request]
  (assoc request
         :all-params
         (merge (if (json/json? request)
                  (:body request)
                  {})
                (:params request)
                (:path-params request)
                (:route-params request))))
