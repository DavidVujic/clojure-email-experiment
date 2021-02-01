(ns app.email
  (:require [clojure.string :as str])
  (:import [javax.mail.internet MimeMessage])) ;; sub dependency of commons-email (see deps.edn)

(defn message-content
  "returns the content of a MimeMessage"
  [message]
  (.getContent message))

(defn- message-content-type [message]
  (.getContentType message))

(defn- message-content-count [content]
  (.getCount content))

(defn- body-part [content index]
  (.getBodyPart content index))

(defn- multipart? [message]
  (str/starts-with? (message-content-type message) "multipart/"))

(defn- multipart->parts [message]
  (let [content       (message-content message)
        content-range (-> content message-content-count range)]
    (map #(body-part content %) content-range)))

(defn- message-parts [message]
  (if (multipart? message)
    (map message-parts (multipart->parts message))
    message))

(defn- msg->map [message]
  {:content-type (message-content-type message)
   :data         message})

(defn- body [message]
  (->> message
       message-parts
       flatten
       (map msg->map)))

(defn- stream->mime-message [stream]
  (MimeMessage. nil stream))

(defn content-types
  "Takes an email as an input-stream and extracts content types into a seq of maps 
   (with the keys :content-type and :data)"
  [email-input-stream]
  (->> email-input-stream
       stream->mime-message
       body))

(defn- content-type? [m type]
  (-> m
      :content-type
      (str/includes? type)))

(defn csv? [m]
  (content-type? m "text/csv"))

(defn content [m]
  (let [data (:data m)]
    (when data (-> data message-content slurp))))

(comment
  (require '[clojure.java.io :as io])
  (->> "emails/example2.eml"
       io/input-stream
       content-types))

(comment
  (require '[clojure.java.io :as io])
  (->> "emails/example4.eml"
       io/input-stream
       content-types
       (filter csv?)
       first
       content))
