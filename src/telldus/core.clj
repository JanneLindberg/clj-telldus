(ns telldus.core
  (:require [clojure.java.io :as io]
            [clojure.string :as s])
  (:import
   (java.net Socket)
   (java.io PrintWriter InputStreamReader BufferedReader)
   (java.io StringWriter)
   )
  )


(defn send-request
  "Sends an request to the specified host and port, returns the response"
  [host port req]
  (with-open [sock (Socket. host port)
              writer (io/writer sock)
              reader (io/reader sock)
              response (StringWriter.)]
    (.append writer (str req))
    (.flush writer)
    (io/copy reader response)
    (s/trim-newline (str response))))


(defn- telldus-arg
  "format a string or integer argument depending on the type"
  [arg]
  (if (number? arg)
    (str "i" arg "s")
    (str (count arg) ":" arg)
    ))


(defn telldus-cmd
  ([cmd]
   (str (count cmd) ":" cmd))

  ([cmd arg]
   (str (count cmd) ":" cmd
        (telldus-arg arg)
        ))

  ([cmd arg arg2]
   (str (count cmd) ":" cmd
        (telldus-arg arg)
        (telldus-arg arg2)
        )))


(defn send-cmd
  "Send command and return the response code or string depending on the format"
  [conn cmd-str]
  (let [resp (send-request (:host conn) (:client-port conn) cmd-str)]
    ;; i<num>s or "<len>:<string>"
    (if (.startsWith resp "i")
      (last (re-find #"i([0-9-]+)s" resp))
      (last (re-find #"(\d+):(.*)" resp))
      )))



(defn telldus
  ([conn cmd]
   (send-cmd conn (telldus-cmd cmd)))
  ([conn cmd arg]
   (send-cmd conn (telldus-cmd cmd arg)))
  ([conn cmd arg arg2]
   (send-cmd conn (telldus-cmd cmd arg arg2)))
  )


(defn get-num-devices
  "Get the number of defined devices"
  [conn]
  (telldus conn "tdGetNumberOfDevices"))
