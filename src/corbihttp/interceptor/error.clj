(ns corbihttp.interceptor.error
  (:require [corbihttp.error :as err]
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
