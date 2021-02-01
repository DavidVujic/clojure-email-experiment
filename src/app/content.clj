(ns app.content
  (:require [clojure.string :as str]
            [app.email :as email]))

(defn- content-type? [m type]
  (-> m
      :content-type
      (str/includes? type)))

(defn csv? [m]
  (content-type? m "text/csv"))

(defn content [m]
  (let [data (:data m)]
    (when data (-> data email/message-content slurp))))

(comment
  (require '[clojure.java.io :as io])
  (->> "emails/example4.eml"
       io/input-stream
       email/content-types
       (filter csv?)
       first
       content))
