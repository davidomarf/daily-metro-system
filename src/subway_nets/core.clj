(ns subway-nets.core
  "This is the main file to schedule the task of running the generator
  and publishing a tweet. When experimenting in the REPL, it's best to
  use the namespace subway-nets.examples"
  (:require [subway-nets.math :as math]
            [subway-nets.line :as sn-l]
            [subway-nets.station :as sn-s]
            [subway-nets.network :as sn-n]
            [quil.core :as q]
            [quil.middleware :as qm]
            [clojure.spec.alpha :as s]))

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