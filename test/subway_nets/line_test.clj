(ns subway-nets.line-test
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as t]
            [subway-nets.spec-check :as sc]
            [subway-nets.line :refer :all]))

(def hundred-tests {:clojure.spec.test.check/opts {:num-tests 100}})

(deftest subway-line-test
  "Check subway-line"
  (is (sc/spec-check `subway-line hundred-tests)))

(deftest add-station-middle-test
  "Check add-station-middle"
  (is (sc/spec-check `add-station-middle hundred-tests)))

(deftest add-station-end-test
  "Check add-station-end"
  (is (sc/spec-check `add-station-end hundred-tests)))

(deftest add-station-start-test
  "Check add-station-start"
  (is (sc/spec-check `add-station-start hundred-tests)))

(deftest extend-terminals-test
  "Check extend-terminals"
  (is (sc/spec-check `extend-terminals hundred-tests)))

; ;; Uncomment following section when debugging. Makes it easier to track bugs.

; (t/check `subway-line hundred-tests)
; (t/check `add-station-middle hundred-tests)
; (t/check `add-station-end hundred-tests)
; (t/check `add-station-start hundred-tests)
; (t/check `extend-terminals hundred-tests)