(ns corbihttp.metric
  (:import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
           io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
           io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
           io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
           io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
           io.micrometer.core.instrument.binder.system.UptimeMetrics
           io.micrometer.core.instrument.binder.system.ProcessorMetrics
           io.micrometer.core.instrument.Counter
           io.micrometer.core.instrument.MeterRegistry
           io.micrometer.core.instrument.Metrics
           io.micrometer.core.instrument.Timer
           io.micrometer.prometheus.PrometheusConfig
           io.micrometer.prometheus.PrometheusMeterRegistry
           java.util.concurrent.TimeUnit))

(defn ->tags
  "Converts a map of tags to an array of string"
  [tags]
  (into-array String
              (->> tags
                   (map (fn [[k v]] [(name k) (name v)]))
                   flatten)))

(defn registry-component
  [tags]
  (let [registry (PrometheusMeterRegistry. PrometheusConfig/DEFAULT)]
    (.commonTags (.config registry) (->tags tags))
    (Metrics/addRegistry registry)
    (.bindTo (ClassLoaderMetrics.) registry)
    (.bindTo (JvmGcMetrics.) registry)
    (.bindTo (JvmMemoryMetrics.) registry)
    (.bindTo (JvmThreadMetrics.) registry)
    (.bindTo (FileDescriptorMetrics.) registry)
    (.bindTo (UptimeMetrics.) registry)
    (.bindTo (ProcessorMetrics.) registry)
    registry))

(defn get-timer!
  "get a timer by name and tags"
  [^MeterRegistry registry n tags]
  (.register (doto (Timer/builder (name n))
               (.publishPercentiles (double-array [0.5 0.75 0.98 0.99]))
               (.tags (->tags tags)))
             registry))

(defn record [^MeterRegistry registry n tags duration]
  (when registry
    (let [timer (get-timer! registry n tags)]
      (.record timer duration TimeUnit/MILLISECONDS))))

(defmacro with-time
  [^MeterRegistry registry n tags & body]
  `(when ~registry
     (let [timer# (get-timer! ~registry ~n ~tags)
           current# (java.time.Instant/now)]
       (try
         (do ~@body)
         (finally
           (let [end# (java.time.Instant/now)]
             (.record timer# (java.time.Duration/between current# end#))))))
     (do ~@body)))

(defn increment!
  "increments a counter"
  ([^MeterRegistry registry counter tags]
   (increment! registry counter tags 1))
  ([^MeterRegistry registry counter tags n]
   (when registry
     (let [builder (doto (Counter/builder (name counter))
                     (.tags (->tags tags)))
           counter (.register builder registry)]
       (.increment counter n)))))

(defn scrape [^PrometheusMeterRegistry registry]
  (some-> registry .scrape))

(defn prom-handler
  [registry]
  (fn handler
    [_]
    {:status 200
     :headers {"Content-Type" "text/plain"}
     :body (.getBytes (scrape registry))}))

(defn http-response
  "updates the http response counter"
  [registry ctx]
  (when registry
    (increment! registry
                :http.responses.total
                {"uri" (str (:uri (:request ctx)))
                 "method"  (str (some-> (:request ctx)
                                        :request-method
                                        name))
                 "status" (str (:status (:response ctx)))})))
