(ns subway-nets.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.spec.alpha :as s]))

; ---------------------------------------------------------------------------- ;
;                               Helping Functions                              ;
; ---------------------------------------------------------------------------- ;

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

; ---------------------------------------------------------------------------- ;
;                                 Map building                                 ;
; ---------------------------------------------------------------------------- ;

(defn subway-line
  "Build a subway line map"
  [id stations]
  {:id id :start (first stations) :end (last stations) :stations stations})

(defn subway-station
  "Build subway station map"
  [id coordinates]
  {:id id :coordinates coordinates})


; ---------------------------------------------------------------------------- ;
;                         Line and Station manipulation                        ;
; ---------------------------------------------------------------------------- ;

; ------------------------------- Extend lines ------------------------------- ;

(defn intermediate-stations
  "Create a new station between every existent station in a vector"
  ([stations]
   (intermediate-stations stations []))
  ([stations new-stations]
   (if (empty? (rest stations))
     (conj new-stations (first stations))
     (recur (rest stations)
            (into new-stations [(first stations) "x"])))))

(defn extend-terminals
  "Create stations at the end and start of a line until reaching a certain length"
  ([line]
   (extend-terminals line 20))
  ([line limit]
   (if (>= (count (:stations line)) limit)
     line
     (recur (add-station-end (add-station-start line "y") "x") limit))))

; --------------------------- Add stations to lines -------------------------- ;

(defn add-station-middle
  "Takes a subway line and inserts the new station in the (floored) middle spot"
  [line station]
  (let [[before after]
        (split-at (int (/ (count (:stations line)) 2))
                  (:stations line))]
    (assoc line :stations (vec (concat before [station] after)))))

(defn add-station-end
  "Takes a line map and inserts a new station at the end of :stations line"
  [line station]
  (assoc (assoc line :stations (into (:stations line) [station])) :end station))

(defn add-station-start
  "Takes a line map and inserts a new station at the start of :stations line"
  [line station]
  (assoc (assoc line :stations (into [station] (:stations line))) :start station))

; ---------------------------------------------------------------------------- ;
;                                Quil functions                                ;
; ---------------------------------------------------------------------------- ;

(defn setup []
  (q/frame-rate 30)
  (q/background 200))

(defn draw []
  (q/stroke (q/random 255))             ;; Set the stroke colour to a random grey
  (q/stroke-weight (q/random 10))       ;; Set the stroke thickness randomly
  (q/fill (q/random 255))               ;; Set the fill colour to a random grey

  (let [diam (q/random 100)             ;; Set the diameter to a value between 0 and 100
        x    (q/random (q/width))       ;; Set the x coord randomly within the sketch
        y    (q/random (q/height))]     ;; Set the y coord randomly within the sketch
    (q/ellipse x y diam diam)))         ;; Draw a circle at x y with the correct diameter

(q/defsketch example
  :title "Metro System"
  :settings #(q/smooth 2)
  :setup setup
  :draw draw
  :size [1080 720])