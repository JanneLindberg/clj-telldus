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



