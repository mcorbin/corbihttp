(ns corbihttp.interceptor.id)

(def request-id
  {:name ::request-id
   :enter (fn [ctx]
            (assoc-in ctx [:request :id] (java.util.UUID/randomUUID)))})
