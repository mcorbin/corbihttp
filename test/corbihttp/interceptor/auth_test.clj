(ns corbihttp.interceptor.auth-test
  (:require [clojure.test :refer :all]
            [corbihttp.b64 :as b64]
            [corbihttp.interceptor.auth :as auth]))

(deftest basic-auth-test
  (let [basic-auth-fn (:enter (auth/basic-auth {:username "Aladdin"
                                                :realm "mirabelle"
                                                :password "open sesame"}))]
    (are [auth-string] (= (merge (auth/forbidden-response "mirabelle")
                                 {:exoscale.interceptor/queue nil
                                  :exoscale.interceptor/stack nil})
                          (basic-auth-fn
                           {:request {:headers {"authorization"
                                                (str "Basic " (b64/to-base64
                                                               auth-string))}}}))
      ""
      "aeaa"
      "Alladin:foo"
      "foo:open sesame")
    (are [auth-string]
        (let [ctx {:request {:headers {"authorization"
                                       (str "Basic " (b64/to-base64
                                                      auth-string))}}} ]
          (= ctx
             (basic-auth-fn
              {:request {:headers {"authorization"
                                   (str "Basic " (b64/to-base64
                                                  auth-string))}}})))
      "Aladdin:open sesame")))
