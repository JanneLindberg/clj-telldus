(ns telldus.core
  (:require [clojure.java.io :as io])
  (:import
   (java.net Socket)
   (java.io PrintWriter InputStreamReader BufferedReader)
   (java.io StringWriter)
   )
  )


(defn send-request
  "Sends an request to the specified host and port"
  [host port req]
  (with-open [sock (Socket. host port)
              writer (io/writer sock)
              reader (io/reader sock)
              response (StringWriter.)]
    (.append writer (str req))
    (.flush writer)
    (io/copy reader response)
    (str response)))


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


(defn telldus
  ([conn cmd]
   (send-request (:host conn) (:client-port conn) (telldus-cmd cmd)))
  ([conn cmd arg]
   (send-request (:host conn) (:client-port conn) (telldus-cmd cmd arg)))
  ([conn cmd arg arg2]
   (send-request (:host conn) (:client-port conn) (telldus-cmd cmd arg arg2)))
  )