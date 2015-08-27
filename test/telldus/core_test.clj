(ns telldus.core-test
  (:require [clojure.test :refer :all]
            [telldus.core :refer :all]))




(deftest test-error-code
  (testing "with string argument"
    (is (= "SUCCESS" (error-code "0")))
    (is (= "ERROR_DEVICE_NOT_FOUND" (error-code "-3")))
    (is (= "ERROR_UNKNOWN"(error-code "-99")))
    (is (nil? (error-code "-100")))
    )

  (testing "with numeric argument"
    (is (= "SUCCESS" (error-code 0)))
    (is (= "ERROR_DEVICE_NOT_FOUND" (error-code -3)))
    (is (= "ERROR_UNKNOWN"(error-code -99)))
    (is (nil? (error-code -100)))
    )

)



(deftest test-device-mapping
  (testing "methods to int"
    (is (= 1 (methods-supported->int :TELLSTICK_TURNON)))
    (is (= 2 (methods-supported->int :TELLSTICK_TURNOFF)))
    (is (= 3 (methods-supported->int :TELLSTICK_TURNON :TELLSTICK_TURNOFF)))

    )

  (testing "with string argument"

    (is (= 8 (count (device-method-value->map "255"))) "Expected all entries")

    )
)


