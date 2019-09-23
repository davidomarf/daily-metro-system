(ns subway-nets.line-test
  (:require [clojure.test :refer :all]
            [clojure.spec.test.alpha :as t]
            [subway-nets.spec-check :as sc]
            [subway-nets.line :refer :all]))

(deftest subway-line-test
  "Check subway-line"
  (is (sc/spec-check `subway-line {})))

(deftest add-station-middle-test
  "Check add-station-middle"
  (is (sc/spec-check `add-station-middle {})))

(deftest add-station-end-test
  "Check add-station-end"
  (is (sc/spec-check `add-station-end {})))

(deftest add-station-start-test
  "Check add-station-start"
  (is (sc/spec-check `add-station-start {})))

(deftest extend-terminals-test
  "Check extend-terminals"
  (is (sc/spec-check `extend-terminals {})))

(t/check `subway-line)
(t/check `add-station-middle)
(t/check `add-station-end)
(t/check `add-station-start)
(t/check `extend-terminals)