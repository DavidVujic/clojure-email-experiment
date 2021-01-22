(ns app.email
  (:require [clojure.string :as str]
            [clojure.java.io :as io])
  (:import [java.io FileInputStream File]
           [javax.mail.internet MimeMessage])) ;; sub dependency of commons-email (see deps.edn)

(defn message-content [msg]
  (.getContent msg))

(defn message-content-type [msg]
  (.getContentType msg))

(defn message-content-count [content]
  (.getCount content))

(defn body-part [content index]
  (.getBodyPart content index))

(defn msg->map [msg]
  {:content-type (message-content-type msg)
   :body         (message-content msg)})

(defn parts [msg]
  (let [content       (message-content msg)
        content-range (-> content message-content-count range)]
    (map #(body-part content %) content-range)))

(defn content-type? [m type]
  (-> m
      :content-type
      (str/includes? type)))

(defn csv? [m]
  (content-type? m "text/csv"))

(defn multipart? [m]
  (str/starts-with? (message-content-type m) "multipart"))

(defn body [msg]
  (if (multipart? msg)
    (->> msg
         parts
         (map msg->map))
    (conj () (msg->map msg))))

(defn ->message [path]
  (with-open [msg (io/input-stream path)]
    (MimeMessage. nil msg)))

(defn parse [s]
  (when s
    (slurp s)))

(comment
  (->> "emails/example_with_attachment"
      ->message
      body
      (filter csv?)
      first
      :body
      parse)
  ,)
