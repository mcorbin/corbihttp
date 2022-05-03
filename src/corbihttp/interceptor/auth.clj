(ns corbihttp.interceptor.auth
  (:require [clojure.string :as string]
            [constance.comp :as constance]
            [corbihttp.b64 :as b64]
            [corbihttp.log :as log]
            [exoscale.cloak :as cloak]
            [exoscale.interceptor :as itc]))


(defn forbidden-response
  [realm]
  {:status 401
   :headers {"WWW-Authenticate" (format "Basic realm=\"%s\"" realm)}})

(defn halt
  [realm]
  (log/info {} "basic auth: invalid credentials")
  (itc/halt (forbidden-response realm)))

(defn check
  [username password realm ctx]
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
            (halt realm)))
        (halt realm)))
    (halt realm)))

(defn basic-auth
  [{:keys [username password realm]}]
  {:name ::basic-auth
   :enter (fn [ctx]
            (check username password realm ctx))})
