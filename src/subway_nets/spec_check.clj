(ns subway-nets.spec-check
  (:require [clojure.spec.test.alpha :as t]
            [clojure.pprint :as pp]))

;; Originally read this implementation at
;;    https://ask.clojure.org/index.php/8590/clojure-spec-alpha-check-inside-clojure-testing-framework

(defn spec-check [fn-to-check options]
  (let [results (t/check [fn-to-check] options)]
    (if (some :failure results)
      (do
        (println "\nFailed specs:")
        (doseq [result results
                :when (:failure result)]
          (println (:sym result))
          (pp/pprint (or (ex-data (:failure result))
                         (:failure result)))))
      true)))