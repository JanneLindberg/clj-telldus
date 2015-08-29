(ns telldus.event-test
  (:import
   (java.io ByteArrayInputStream)
   (java.nio.charset StandardCharsets))
  (:require [clojure.test :refer :all]
            [telldus.core :refer :all]
            [telldus.event :refer :all]
            ))


(def
  ^{:private true :doc "A simple test message" }
  event-msg-1
  "16:TDRawDeviceEvent95:class:command;protocol:arctech;model:selflearning;house:11998877;unit:2;group:0;method:turnoff;i1s")


(deftest test-1
  (testing "message-from-stream"
    (let [in-stream (ByteArrayInputStream. (.getBytes event-msg-1 StandardCharsets/UTF_8))
          resp-1 (get-message-from-stream in-stream)
          resp-2 (get-message-from-stream in-stream)
          ]

      (is (string? resp-1) "Expected a string")

      (is (= 120 (count resp-1)) "expected content")

      (is (= 0 (count resp-2)) "expected nothing")

      )))


(deftest test2
  (testing "reponse message handling"
    (let [resp (get-response-message event-msg-1)]

      (is (map? resp) "Expected a map")

      (is (= (:event resp) "TDRawDeviceEvent"))

      (is (= (:method resp) "turnoff"))

      )))




