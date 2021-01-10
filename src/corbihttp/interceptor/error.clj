(ns corbihttp.interceptor.error
  (:require [corbihttp.error :as err]
            [corbihttp.log :as log]
            [corbihttp.metric :as metric]
            [exoscale.ex :as ex]))

(defn handle-error
  [request e]
  (cond
    (ex/type? e ::ex/invalid-spec) (err/handle-spec-error request e)
    (ex/type? e :corbi/user) (err/handle-user-error request e)
    :else (err/handle-unexpected-error request e)))

(def error
  {:name ::error
   :error (fn [ctx e]
            (assoc ctx :response (handle-error (:request ctx) e)))})

(defn last-error
  [registry]
  {:name ::last-error
   :error (fn [ctx e]
            (metric/increment! registry
                               :http.fatal.error.total
                               {})
            (log/error e "fatal error")
            (assoc ctx :response {:status 500
                                  :body {:error err/default-msg}}))})
