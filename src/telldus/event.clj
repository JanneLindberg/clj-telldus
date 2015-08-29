(ns telldus.event
  (:gen-class)
  (:import
   (java.net Socket)
   (org.telldus Util))
  (:require [clojure.java.io :as io]
            [telldus.core :as telldus]
            ))

(def active (atom true))


(defn response->map
  "chopp up the response string and return as a map"
  [data]
  (into {} (for [[k v] (partition 2 (clojure.string/split data #"(:|;)+"))]
             [(keyword k) v])))


(defn get-response-message [event-msg]
  (let [[_ event _ msg _]
        (drop 1 (re-find #"(\d+):([A-Za-z]*)(\d+):(.*?)(i1s)(.*)" event-msg))
        msg-map(response->map msg)]
    (merge {:event event} msg-map )))


(defn get-message-from-stream [stream]
  (apply str (Util/getMessage stream)))


(defn event-listener
  [conn]
  (with-open [socket (Socket. (:host conn) (:event-port conn))]
    (loop [msg-in (get-message-from-stream (.getInputStream socket))]
      (when-let [handler (:handler conn)]
        (handler msg-in))
      (when-not (true? @active)
        nil)
      (recur (get-message-from-stream socket)))))


