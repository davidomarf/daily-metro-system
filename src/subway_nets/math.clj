(ns subway-nets.math
  "Provides mathematical operations that may be used across all the codebase"
  (:require [quil.core :as q]))

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

(defn dist
  "Call q/dist using two 2D vectors instead of four coordinates"
  [a b]
  (q/dist (first a) (second a) 
          (first b) (second b)))

(defn nearest-neighbor
  "Returns the nearest neighbor for a point. Neighbors must be a vector of
   vectors [x y ...]. Extra elements are still there in the return value"
  ([neighbors point]
   (nearest-neighbor (rest neighbors)
                     point
                     [(first neighbors) (dist (first neighbors) point)]))
  ([neighbors point current-nearest]
   (if (empty? neighbors)
     current-nearest
     (let [next [(first neighbors) (dist (first neighbors) point)]
           nearest (if (< (second next) (second current-nearest))
                     next
                     current-nearest)]
       (recur (rest neighbors)
              point
              nearest)))))

(comment
  (do
  ;; Current nearest-neighbor is computed using a naive approach.
  ;; Consider using abscondment/clj-kdtree for a more efficient solution.
    (def points [[10 20] [30 40] [50 60] [60 50] [40 30] [20 10]])
    (def x [(rand 60) (rand 60)])
    x
    (nearest-neighbor points x)
    ;;
    )
)