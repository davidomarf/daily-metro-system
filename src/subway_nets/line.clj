(ns subway-nets.line
  "Provides the functions to operate over or with subway lines"
  (:require [subway-nets.math :as math]
            [subway-nets.station :as sn-s]))

; ------------------------------- Create a line ------------------------------ ;

(defn subway-line
  "Build a subway line map"
  [id stations]
  {:id id :start (first stations) :end (last stations) :stations stations})


; --------------------------- Add stations to lines -------------------------- ;

(defn add-station-middle
  "Takes a subway line and inserts the new station in the (floored) middle spot"
  [line station]
  (if (>= (count (:stations line)) 2)
    (let [[before after]
          (split-at (int (/ (count (:stations line)) 2))
                    (:stations line))]
      (assoc line :stations (vec (concat before [station] after))))))
      
(defn add-station-end
  "Takes a line map and inserts a new station at the end of :stations line"
  [line station]
  (assoc (assoc line :stations (into (:stations line) [station])) :end station))

(add-station-end (subway-line "" []) "x")

(defn add-station-start
  "Takes a line map and inserts a new station at the start of :stations line"
  [line station]
  (assoc (assoc line :stations (into [station] (:stations line))) :start station))


; ------------------------------- Extend a line ------------------------------ ;

(defn extend-terminals
  "Create stations at the end and start of a line until reaching a certain length"
  ([line]
   (extend-terminals line 20))
  ([line limit]
   (if (<= limit 0)
     nil
     (let [size (count (:stations line))]
       (if (>= size limit)
         line
         (if (= (inc size) limit)
           (add-station-start line "y")
           (recur (add-station-end (add-station-start line "y") "x") limit)))))))
