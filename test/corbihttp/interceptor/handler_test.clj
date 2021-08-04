(ns corbihttp.interceptor.handler-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [corbihttp.interceptor.handler :as handler]))

(s/def ::name keyword?)
(s/def ::get-user (s/keys :req-un [::name]))

(defn handler-fn-builder
  [state]
  (fn [_ params]
    (swap! state conj params)
    {:ok true}))

(defn dispatch-map
  [handler-fn]
  {:user/get {:path [#"api/v1/user/" :name #"/?"]
              :method :get
              :spec ::get-user
              :handler-fn handler-fn}
   :user/create {:path [#"api/v1/user/" :name #"/?"]
                 :method :post
                 :handler-fn handler-fn}
   :system/healthz {:path [#"healthz/?"]
                    :method :get
                    :handler-fn handler-fn}})

(deftest main-handler-test
  (let [state (atom [])
        h (:enter (handler/main-handler {:registry nil
                                         :dispatch-map (dispatch-map (handler-fn-builder state))
                                         :handler-component nil
                                         :not-found-handler (fn [_ _] {:status 404})}))]
    (testing "get user"
      (is (= {:ok true}
             (:response (h {:handler :user/get
                            :request {:request-method :get
                                      :route-params {:name "foo"}
                                      :uri "/api/v1/user/foo"}}))))
      (is (=  {:request-method :get
               :uri "/api/v1/user/foo"
               :route-params {:name "foo"}
               :all-params {:name :foo}}
              (-> @state last))))
    (testing "create user"
      (is (= {:ok true}
             (:response (h {:handler :user/create
                            :request {:request-method :post
                                      :route-params {:name "foo"}
                                      :uri "/api/v1/user/foo"}}))))
      (is (=  {:request-method :post
               :uri "/api/v1/user/foo"
               :route-params {:name "foo"}
               :all-params {:name "foo"}}
              (-> @state last))))
    (testing "create another user"
      (is (= {:ok true}
             (:response (h {:request {:request-method :post
                                      :route-params {:name "foo-bar"}
                                      :uri "/api/v1/user/foo-bar"}
                            :handler :user/create}))))
      (is (=  {:request-method :post
               :uri "/api/v1/user/foo-bar"
               :route-params {:name "foo-bar"}
               :all-params {:name "foo-bar"}}
              (-> @state last))))
    (testing "healthz"
      (is (= {:ok true}
             (:response (h {:handler :system/healthz
                            :request {:request-method :get
                                      :uri "/healthz"}}))))
      (is (=  {:request-method :get
               :uri "/healthz"
               :all-params {}}
              (-> @state last))))))
