(ns phonebookapi.handler
  (:require [phonebookapi.phonebookstore :as store]
            [taoensso.timbre :as log]
            [reitit.ring :as ring]
            [ring.middleware.cors :refer [wrap-cors]]
            [muuntaja.core :as m]
            [reitit.ring.middleware.exception :refer [exception-middleware]]
            [reitit.ring.middleware.parameters :refer [parameters-middleware]]
            [reitit.ring.middleware.muuntaja :refer [format-negotiate-middleware
                                                     format-request-middleware
                                                     format-response-middleware]]))

(defn search-handler [req]
  (let [{:strs [name]} (-> req :query-params)
        resp (store/search-items name)]
    {:status (if resp 200 400)
     :body resp}))

(defn delete-person-handler [req]
  (let [{:keys [first-name last-name]} (-> req :path-params)
        resp (store/remove-item! first-name last-name)]
    {:status (if resp 200 404)
     :body {:success resp}}))

(defn add-person-handler [req]
  (let [{:keys [first-name last-name phone-num]} (-> req :body-params)
        resp (store/add-item! first-name last-name phone-num)]
    {:status (if resp 200 404)
     :body resp}))

(defn get-person-handler [req]
  (let [{:strs [first-name last-name]} (-> req :query-params)
        resp (store/get-item first-name last-name)]
    {:status (if resp 200 404)
     :body resp}))

(def routes
  ["/phonebook"
   ["/" {:post add-person-handler
         :get get-person-handler}]
   ["/search" {:get search-handler}]
   ["/:first-name/:last-name" {:delete delete-person-handler}]])

(defn handler []
  (wrap-cors (ring/ring-handler (ring/router
                                 routes
                                 {:data {:muuntaja   m/instance
                                         :middleware [parameters-middleware
                                                      format-negotiate-middleware
                                                      format-request-middleware
                                                      exception-middleware
                                                      format-response-middleware]}}))

             :access-control-allow-origin [#"http://localhost:8081"]
             :access-control-allow-credentials true
             :access-control-allow-methods [:get :post :put :delete]))
