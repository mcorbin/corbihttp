(ns corbihttp.interceptor.auth
  (:require [clojure.string :as string]
            [constance.comp :as constance]
            [corbihttp.b64 :as b64]
            [corbihttp.log :as log]
            [exoscale.cloak :as cloak]
            [exoscale.interceptor :as itc]))


(def forbidden-response {:status 401
                         :headers {"WWW-Authenticate" "Basic realm=\"mirabelle\""}})

(defn halt
  []
  (log/info {} "basic auth: invalid credentials")
  (itc/halt forbidden-response))

(defn basic-auth
  [{:keys [username password]}]
  {:name ::basic-auth
   :enter (fn [ctx]
            (if-let [auth-header (get-in ctx [:request :headers "authorization"])]
              (let [[basic payload] (string/split auth-header #" ")]
                (if (and (not (string/blank? basic))
                         (not (string/blank? payload))
                         (= "Basic" basic))
                  (let [[n pass] (-> (b64/from-base64 payload)
                                     (string/split #":"))]
                    (if (and (constance/constant-string= username n)
                             (constance/constant-string= pass (cloak/unmask password)))
                      ctx
                      (halt)))
                  (halt)))
              (halt)))})
