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
   :not-found {:path true
               :handler-fn handler-fn}})

(deftest main-handler-test
  (let [state (atom [])
        h (:enter (handler/main-handler nil
                                        (dispatch-map (handler-fn-builder state))
                                        nil))]
    (testing "get user"
      (is (= {:ok true}
             (:response (h {:request {:request-method :get
                                      :uri "/api/v1/user/foo"}}))))
      (is (=  {:request-method :get
               :uri "/api/v1/user/foo"
               :route-params {:name "foo"}
               :all-params {:name :foo}
               :handler :user/get}
              (-> @state last))))
    (testing "create user"
      (is (= {:ok true}
             (:response (h {:request {:request-method :post
                                      :uri "/api/v1/user/foo"}}))))
      (is (=  {:request-method :post
               :uri "/api/v1/user/foo"
               :route-params {:name "foo"}
               :all-params {:name "foo"}
               :handler :user/create}
              (-> @state last))))
    (testing "not found"
      (is (= {:ok true}
             (:response (h {:request {:request-method :post
                                      :uri "/abc"}}))))
      (is (=  {:request-method :post
               :uri "/abc"
               :all-params {}
               :handler :not-found}
              (-> @state last))))))
