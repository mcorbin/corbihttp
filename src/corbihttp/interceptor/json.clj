(ns corbihttp.interceptor.json
  (:require [byte-streams :as bs]
            [cheshire.core :as json]
            [exoscale.ex :as ex]))

(def json
  {:name ::json
   :leave (fn [ctx]
            (if (coll? (get-in ctx [:response :body]))
              (-> (update-in ctx [:response :body] json/generate-string)
                  (update-in [:response :headers] assoc "content-type"
                             "application/json"))
              ctx))})

(defn json?
  [request]
  (= (-> request :headers (get "content-type"))
     "application/json"))

(def request-params
  {:name ::json-params
   :enter (fn [ctx]
            (let [request (:request ctx)]
              (if (and (:body request)
                       (json? request))
                (try
                  (update-in ctx
                             [:request :body]
                             (fn [body]
                               (-> (bs/convert body String)
                                   (json/parse-string true))))
                  (catch Exception _
                    (throw (ex/ex-info "Fail to convert the request body to json"
                                       [::invalid-json [:corbi/user ::ex/incorrect]]))))
                ctx)))})
