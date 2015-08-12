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


(defn get-device-info
  "Get info on the device specified by the id"
  [conn id]
  (let [name (telldus conn "tdGetName" id)
        model (telldus conn "tdGetModel" id)
        last-command (telldus conn "tdLastSentCommand" id 255)]
    {:id id :name name :model model :command last-command}
    ))


(defn get-all-device-info
  "List of all configurated devices"
  [conn]
  (let [arr (atom []) ]
    (doseq [i (range (Integer. (get-num-devices conn)))]
      (let [id (Integer. (telldus conn "tdGetDeviceId" i))]
        (swap! arr conj (get-device-info conn id))))
    @arr))



(def
  ^{:private true
   :doc "Device methods"
    }
  device-methods {
                  :TELLSTICK_TURNON    1
                  :TELLSTICK_TURNOFF   2
                  :TELLSTICK_BELL      4
                  :TELLSTICK_TOGGLE    8
                  :TELLSTICK_DIM       16
                  :TELLSTICK_LEARN     32
                  :TELLSTICK_EXECUTE   64
                  :TELLSTICK_UP        128
                  :TELLSTICK_DOWN      256
                  :TELLSTICK_STOP      512
                  })


(defn methods-supported->int
  "Convert a list of device methods keywords into the an integer value"
  [& args]
  (apply + (map #(%1 device-methods) args )))


(defmulti device-method-value->map
  "Convert a device method value into the map representation"
  class)

(defmethod device-method-value->map Integer [arg]
  (filter #(not (zero? (bit-and arg (val %1)))) device-methods))

(defmethod device-method-value->map Long [arg]
  (device-method-value->map (Integer. arg)))

(defmethod device-method-value->map String [arg]
  (device-method-value->map (Integer. arg)))


;;
;; Error codes
;;
(defmulti error-code
  "Convert a numeric error code to a string representation"
  class)

(defmethod error-code String [code]
  (error-code (Long. code)))

(defmethod error-code Long [code]
  (let [error-codes {
                     0 "SUCCESS"
                     -1 "ERROR_NOT_FOUND"
                     -2 "ERROR_PERMISSION_DENIED"
                     -3 "ERROR_DEVICE_NOT_FOUND"
                     -4 "ERROR_METHOD_NOT_SUPPORTED"
                     -5 "ERROR_COMMUNICATION"
                     -6 "ERROR_CONNECTING_SERVICE"
                     -7 "ERROR_UNKNOWN_RESPONSE"
                     -8 "ERROR_SYNTAX"
                     -9 "ERROR_BROKEN_PIPE"
                    -10 "ERROR_COMMUNICATING_SERVICE"
                    -11 "ERROR_CONFIG_SYNTAX"
                    -99 "ERROR_UNKNOWN"}]
    (error-codes code)))



(defn turn-on
  "Turn on an device"
  [conn device-id]
  (telldus conn "tdTurnOn" device-id))

(defn turn-off
  "Turn off an device"
  [conn device-id]
  (telldus conn "tdTurnOff" device-id))

