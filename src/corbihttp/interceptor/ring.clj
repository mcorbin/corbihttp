(ns corbihttp.interceptor.ring
  (:require [ring.middleware.cookies :as cookies]
            [ring.middleware.params :as params]
            [ring.middleware.proxy-headers :as proxy-headers]
            [ring.middleware.keyword-params :as keyword-params]))

(def keyword-params
  {:name ::keyword-params
   :enter (fn [ctx] (update ctx :request keyword-params/keyword-params-request))})

(def params
  {:name ::params
   :enter (fn [ctx] (update ctx :request params/params-request))})

(def forwarded-remote-addr
  {:name ::forwarded-remote-addr
   :enter (fn [ctx] (update ctx :request proxy-headers/forwarded-remote-addr-request))})

(def cookies
  {:name ::cookies
   :enter (fn [ctx] (update ctx :request #(cookies/cookies-request % {})))
   :leave (fn [ctx] (update ctx :response #(cookies/cookies-response % {})))})
