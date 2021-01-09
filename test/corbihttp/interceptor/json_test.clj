(ns corbihttp.interceptor.json-test
  (:require [clojure.test :refer :all]
            [corbihttp.interceptor.json :as json]))

(deftest json-test
  (is (= {:response {:body "{\"foo\":\"bar\"}"
                     :headers {"content-type" "application/json"}}}
       ((:leave json/json) {:response {:body {:foo "bar"}}}))))

(deftest request-params-test
  (is (= ((:enter json/request-params) {:request {:body "{\"foo\":\"bar\"}"}})
         {:request {:body {:foo "bar"}}}))
  (is (thrown-with-msg?
       Exception
       #"Fail to convert the request body to json"
       ((:enter json/request-params) {:request {:body "{\"foo\":\"ba"}}))))
