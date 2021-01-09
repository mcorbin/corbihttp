(ns corbihttp.interceptor.response-test
  (:require [clojure.test :refer :all]
            [corbihttp.interceptor.response :as response]))

(deftest response-test
  (is (= {:body "foo"}
         ((:leave response/response) {:response {:body "foo"}}))))
