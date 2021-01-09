(ns corbihttp.interceptor.id-test
  (:require [clojure.test :refer :all]
            [corbihttp.interceptor.id :as id]))

(deftest request-id-test
  (is (uuid? (get-in ((:enter id/request-id) {:request {}})
                     [:request :id]))))
