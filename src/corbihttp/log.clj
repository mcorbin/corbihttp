(ns corbihttp.log
  "Functions for structured logging"
  (:require [unilog.context :as context]
            [clojure.tools.logging :as log]))

(defmacro info
  [data & args]
  `(context/with-context~ data
    (log/info ~@args)))

(defmacro infof
  [data & args]
  `(context/with-context~ data
    (log/infof ~@args)))

(defmacro debug
  [data & args]
  `(context/with-context~ data
    (log/debug ~@args)))

(defmacro error
  [data & args]
  `(context/with-context~ data
    (log/error ~@args)))

(defn req-ctx
  [request]
  {:request-id (:id request)
   :request-uri (:uri request)
   :request-method (:request-method request)})
