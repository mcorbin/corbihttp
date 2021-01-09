(ns corbihttp.interceptor.error-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [corbihttp.interceptor.error :as error]
            [exoscale.ex :as ex]))

(s/def ::id uuid?)
(s/def ::name string?)
(s/def ::test (s/keys :req-un [::id ::name]))

(deftest request-id-test
  (let [handler (:error error/error)]
    (is (= {:response {:status 500, :body {:error "Internal error."}}}
           (handler {} (ex-info "an ex info" {}))))
    (is (= {:response {:status 500, :body {:error "Internal error."}}}
           (handler {} (ex/ex-incorrect "incorrect" {}))))
    (is (= {:response {:status 400, :body {:error "incorrect"}}}
           (handler {} (ex/ex-info "incorrect" [:corbi/user
                                                [::ex/incorrect]]))))
    (is (= {:response {:status 400,
                       :body {:error "field id is missing, field name is missing"}}}
           (handler {} (try (ex/assert-spec-valid ::test {})
                            (catch Exception e e)))))
    (is (= {:response {:status 400,
                       :body {:error "invalid value {}"}}}
           (handler {} (try (ex/assert-spec-valid ::id {})
                            (catch Exception e e)))))))
