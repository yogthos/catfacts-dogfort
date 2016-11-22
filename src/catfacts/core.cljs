(ns catfacts.core
  (:use-macros [dogfort.middleware.routes-macros :only [defroutes GET]])
  (:require [dogfort.http :refer [run-http]]
            [cljs.nodejs]
            [cljs.reader :as reader]
            [redlobster.promise :as p]
            [dogfort.middleware.routes :refer [routes]]))

(cljs.nodejs/enable-util-print!)

(def fs (js/require "fs"))

(defn read-file [file]
  (.readFileSync fs file "utf8"))

(defn json-response [m]
  (-> m clj->js js/JSON.stringify))

(defn catfact [{:keys [facts fact-count]}]
  (let [idx (rand-int fact-count)
        fact (get-in facts [idx])]
    (str "Cat Fact " (inc idx) ": " fact "\n:cat: :cat: :cat:")))

(defn app [facts]
  (fn [request]
    (p/promise
     {:status 200
      :body (json-response
             {:response_type "in_channel"
              :text (catfact facts)})}
     #_(GET "/" req
                   {:status 200
                    :body (json-response
                           {:response_type "in_channel"
                            :text (catfact facts)})}))))

(defn main [& args]
  (let [fact-data (-> "catfacts.edn" read-file reader/read-string)
        handler (app {:facts fact-data
                      :fact-count (count fact-data)})]
    (run-http
     handler
     {:port (or (-> js/process .-env .-PORT) 5000)})))

(set! *main-cli-fn* main)
