(ns subway-nets.line-test
  (:require [clojure.test :refer :all]
            [subway-nets.spec-check :as sc]
            [subway-nets.line :refer :all]))

(deftest subway-line-test
  "Check subway-line"
  (is (sc/spec-check `subway-line {})))