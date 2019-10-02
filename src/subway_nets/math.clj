(ns subway-nets.math
  "Provides mathematical operations that may be used across all the codebase"
  (:require [clojure.math.numeric-tower :as math]
            [quil.core :as q]))

(def PI Math/PI)
(def PI2 (* PI 2))

(defn close-enough?
  "Determine if two points are separated by a distance smaller than a threshold"
  [a b threshold]
  (< (q/dist (:x a) (:y a) (:x b) (:y b)) threshold))

(defn angle-between-points
  "Return the angle in rads formed between point a and b"
  [a b]
  (q/atan2 (- (:y b) (:y a)) (- (:x b) (:x a))))

(defn displace-point
  "Applies a displacement of custom angle and distance for a given point"
  [point angle distance]
  {:x (+ (:x point) (* (q/cos angle) distance))
   :y (+ (:y point) (* (q/sin angle) distance))})