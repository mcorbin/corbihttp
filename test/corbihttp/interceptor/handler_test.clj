(ns corbihttp.interceptor.handler-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [corbihttp.interceptor.handler :as handler]
            [corbihttp.interceptor.route :as route]))

(s/def ::name keyword?)
(s/def ::get-user (s/keys :req-un [::name]))

(defn handler-fn-builder
  [state]
  (fn [_ params]
    (swap! state conj params)
    {:ok true}))

(defn router
  [handler-fn]
  [["/api/v1/user/:name" {:get {:spec ::get-user
                                :handler handler-fn}
                          :post {:handler handler-fn}}]
   ["/healthz" {:get {:handler handler-fn}}]])

(deftest main-handler-test
  (let [state (atom [])
        r (:enter (route/route {:router (router (handler-fn-builder state))}))
        h (:enter (handler/main-handler {:registry nil
                                         :handler-component nil}))]
    (testing "get user"
      (is (= {:ok true}
             (:response (h (r {:request {:request-method :get
                                         :uri "/api/v1/user/foo"}})))))
      (is (=  {:request-method :get
               :uri "/api/v1/user/foo"
               :path-params {:name "foo"}
               :all-params {:name :foo}}
              (-> @state last))))
    (testing "create user"
      (is (= {:ok true}
             (:response (h (r {:request {:request-method :post
                                         :uri "/api/v1/user/foo"}})))))
      (is (=  {:request-method :post
               :uri "/api/v1/user/foo"
               :path-params {:name "foo"}
               :all-params {:name "foo"}}
              (-> @state last))))
    (testing "create another user"
      (is (= {:ok true}
             (:response (h (r {:request {:request-method :post
                                         :uri "/api/v1/user/foo-bar"}})))))
      (is (=  {:request-method :post
               :uri "/api/v1/user/foo-bar"
               :path-params {:name "foo-bar"}
               :all-params {:name "foo-bar"}}
              (-> @state last))))
    (testing "healthz"
      (is (= {:ok true}
             (:response (h (r {:request {:request-method :get
                                         :uri "/healthz"}})))))
      (is (=  {:request-method :get
               :uri "/healthz"
               :path-params {}
               :all-params {}}
              (-> @state last))))
    (testing "path not found"
      (is (thrown-with-msg?
           Exception
           #"not found"
           (h (r {:request {:request-method :get
                            :uri "/healthz/"}})))))
    (testing "method not found"
      (is (thrown-with-msg?
           Exception
           #"not found"
           (h (r {:request {:request-method :post
                            :uri "/healthz"}})))))))
