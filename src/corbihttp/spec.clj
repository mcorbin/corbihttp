(ns corbihttp.spec
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [exoscale.cloak :as cloak])
  (:import org.apache.commons.validator.routines.InetAddressValidator))

(s/def ::port (s/int-in 1 65536))

(s/def ::ne-string (s/and string? (complement string/blank?)))
(s/def ::host ::ne-string)
(s/def ::secret cloak/secret?)
(s/def ::keyword-or-str (s/or :keyword keyword?
                              :string ::ne-string))

(s/def ::file-spec (fn [path]
                     (let [file (io/file path)]
                       (and (.exists file)
                            (.isFile file)))))
(s/def ::directory-spec (fn [path]
                          (let [file (io/file path)]
                            (and (.exists file)
                                 (.isDirectory file)))))

(s/def ::cacert ::file-spec)
(s/def ::cert ::file-spec)
(s/def ::key ::file-spec)

(def ^InetAddressValidator validator (InetAddressValidator/getInstance))

(s/def ::ip (fn [s] (and (.isValid validator s)
                         (not (string/includes? s "/")))))
(s/def ::ipv4 (fn [s] (.isValidInet4Address validator s)))
(s/def ::ipv6 (fn [s] (and (.isValidInet6Address validator s)
                           ;; the validator accepts prefix
                           (not (string/includes? s "/")))))
