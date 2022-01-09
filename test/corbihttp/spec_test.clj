(ns corbihttp.spec-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :refer :all]
            [corbihttp.spec :as spec]))

(deftest ip-spec-test
  (is (s/valid? ::spec/ip "10.0.0.1"))
  (is (s/valid? ::spec/ip "::10"))
  (is (not (s/valid? ::spec/ip "1")))
  (is (s/valid? ::spec/ip "::1"))
  (is (s/valid? ::spec/ip "2001:db8:0:85a3::ac1f:8001"))
  (is (s/valid? ::spec/ip "2001:0db8:0000:85a3:0000:0000:ac1f:8001"))
  (is (not (s/valid? ::spec/ip "2001:0db8:0000:85a3:0000:0000:ac1f:8001/8")))
  (is (s/valid? ::spec/ipv4 "10.0.0.1"))
  (is (not (s/valid? ::spec/ipv4 "2001:0db8:0000:85a3:0000:0000:ac1f:8001/8")))
  (is (not (s/valid? ::spec/ipv4 "1")))
  (is (not (s/valid? ::spec/ipv4 "foo")))
  (is (not (s/valid? ::spec/ipv6 "10.0.0.1")))
  (is (not (s/valid? ::spec/ipv6 "10.0.0.1"))))
