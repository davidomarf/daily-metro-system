(ns subway-nets.line-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [subway-nets.line :refer :all]))

;; When lost, check:
;;   https://github.com/clojure/test.check#testcheck
;;   https://github.com/clojure/test.check/blob/master/doc/intro.md

(def subway-tuple
  "Generator for subway tuples: [ '' [] ]"
  (gen/tuple gen/string-alphanumeric (gen/vector gen/string-alphanumeric)))

(def subway-line-prop
  (prop/for-all [[g-id g-stations] subway-tuple] 
                (let [s (subway-line g-id g-stations)]
                  (and (= g-id (:id s))
                       (= (count g-stations) (count (:stations s)))
                       (= g-stations (:stations s))
                       (= (:start s) (first (:stations s)))
                       (= (:end s) (last (:stations s)))))))

(def add-station-middle-prop
  (prop/for-all [[g-id g-stations] subway-tuple
                 g-station-id gen/string-alphanumeric]
                (let [line (subway-line g-id g-stations)
                      return (add-station-middle line g-station-id)]
                  (or 
                   ;; Line size is smaller than 2 (there can't exist a middle)
                   (and (< (count (:stations line)) 2)
                        (nil? return))
                   
                   ;; Default run (line size >= 2)
                   (and (= (:id line) (:id return))
                        (= (inc (count (:stations line))) (count (:stations return)))
                        (some #{g-station-id} (:stations return))
                        (= (:start line) (:start return))
                        (= (:end line) (:end return)))))))

(def add-station-end-prop
  (prop/for-all [[g-id g-stations] subway-tuple
                 g-station-id gen/string-alphanumeric]
                (let [line (subway-line g-id g-stations)
                      return (add-station-end line g-station-id)]
                  (and (= (:id line) (:id return))
                       (= (inc (count (:stations line))) (count (:stations return)))
                       (some #{g-station-id} (:stations return))
                       (= (:start line) (:start return))
                       (= (:end return) g-station-id)))))

(def add-station-start-prop
  (prop/for-all [[g-id g-stations] subway-tuple
                 g-station-id gen/string-alphanumeric]
                (let [line (subway-line g-id g-stations)
                      return (add-station-start line g-station-id)]
                  (and (= (:id line) (:id return))
                       (= (inc (count (:stations line))) (count (:stations return)))
                       (some #{g-station-id} (:stations return))
                       (= (:end line) (:end return))
                       (= (:start return) g-station-id)))))

(def extend-terminals-prop
  (prop/for-all [[g-id g-stations] subway-tuple
                 limit gen/small-integer]
                (let [line (subway-line g-id g-stations)
                      return (extend-terminals line limit)]
                  (or
                   ;; Limit is negative or equal to zero
                   (and (<= limit 0)
                        (nil? return))

                   ;; Limit is smaller or equal to original size
                   (and (<= limit 
                            (count (:stations line)))
                        (= line return))

                   ;; Default run (limit > original size)
                   (or
                     ;; Limit = original size + 1
                    (and (=  limit
                             (count (:stations return)))
                         (not= (:start line)
                               (:start return)))
                     ;; Limit > original size + 1
                    (and (> limit 
                            (inc (count (:stations line))))
                         (not= (:start line)
                               (:start return))
                         (not= (:end line) 
                               (:end return))))))))


(tc/quick-check 20 subway-line-prop)
(tc/quick-check 20 add-station-middle-prop)
(tc/quick-check 20 add-station-end-prop)
(tc/quick-check 20 add-station-start-prop)
(tc/quick-check 20 extend-terminals-prop)
