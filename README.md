# clj-telldus
Clojure library to communicate with the telldusd daemon

## Usage
This is an initial, experimental implementation that currently only supports sending commands to a Tellstick Duo interface.
No support for listening to any events from the interface has been implemented so far.

###
Start the telldus daemon with the script below, or in a similar way. This script starts the telldus daemon and publish the
UNIX socket as TCP sockets.


```sh
#!/bin/sh
sudo telldusd --debug --nodaemon &

sudo socat TCP-LISTEN:12000,reuseaddr,fork UNIX-CLIENT:/tmp/TelldusClient &
sudo socat TCP-LISTEN:12001,reuseaddr,fork UNIX-CLIENT:/tmp/TelldusEvents &
```



## Some basic examples
This assumes that the Tellstick Duo has been properly configurated as described here [http://developer.telldus.com/wiki/TellStick_conf]

### Device commands

```clojure
(use '[telldus.core :as telldus])
```


```clojure
(def conn {
           :host "HOSTIP"
           :client-port 12000
           :event-port 12001
           })
```


#### Turn a device on or off
```clojure

;; Turn on device 100
(telldus/telldus conn "tdTurnOn" 100)


;; Turn off device 100
(telldus/telldus conn "tdTurnOff" 100)

```

### Event listener
Below is an example how to configure and start the event-listener, the handler in this example
simply prints the response message map.

```clojure
(ns telldus.test
  (:require [telldus.core :as telldus])
  (:use [telldus.event :as event :only [event-listener get-response-message]]))


(defn message-handler [event-message]
  (println (event/get-response-message event-message)))

(def conn {
           :host "<HOSTIP>"
           :client-port 12000
           :event-port 12001
           :handler message-handler
           })
;;
(future (event/event-listener conn))

```


### Misc operations
```clojure
;; get the serial id from the device
(telldus/telldus conn "tdControllerValue"  1 "serial")

;; get the firmware revison from the interface device
(telldus/telldus conn "tdControllerValue"  1 "firmware")

```


### Print configurated devices
```clojure
;; get a list of configurated devices
(doseq [i (range (Integer. (telldus/get-num-devices conn)))]
  (let [dev-id (Integer. (telldus/telldus conn "tdGetDeviceId" i))
        name (telldus/telldus conn "tdGetName" dev-id)
        model (telldus/telldus conn "tdGetModel" dev-id)]

    (println "id:" dev-id "  " name "  model:" model)
    )
)
```



## License

Copyright (c) 2015 JanneLindberg

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
