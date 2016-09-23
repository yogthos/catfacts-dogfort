(ns catfacts.core
  (:use-macros [dogfort.middleware.routes-macros :only [GET]])
  (:require [dogfort.http :refer [run-http]]
            [cljs.nodejs]
            [cljs.reader :as reader]
            [dogfort.middleware.defaults :as defaults]
            [dogfort.middleware.routes]))

(def fs (js/require "fs"))

(defn catfact [facts]
  (let [idx (rand-int (:fact-count facts))
        fact (get-in facts [:facts idx])]
    (str "Cat Fact " (inc idx) ": " fact "\n:cat: :cat: :cat:")))

(cljs.nodejs/enable-util-print!)

(defn server [facts]
  (-> (GET "/" req
           {:status 200
            :body (-> {:response_type "in_channel"
                       :text (catfact facts)}
                      (clj->js)
                      (js/JSON.stringify))})
      (defaults/wrap-defaults {})
      (run-http {:port (or (.-PORT (.-env js/process)) 5000)})))

(defn start-app [err data]
  (if err
    (throw (js/Error. err))
    (let [fact-data (reader/read-string data)
          facts {:facts fact-data
                 :fact-count (count fact-data)}]
      (server facts))))

(defn main [& args]
  (.readFile fs "catfacts.edn" "utf8" start-app))

(set! *main-cli-fn* main)
