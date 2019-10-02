(ns subway-nets.station
  "Provides the functions to operate over or with subway stations"
  (:require [subway-nets.math :as math]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as t]))

; ----------------------------- Create a station ----------------------------- ;

(defn subway-station
  "Build subway station map"
  ([id coordinates]
   (subway-station id coordinates (rand math/PI2)))
  ([id coordinates angle]
   {:id id :coordinates coordinates :angle angle}))

(s/fdef subway-station
  :args (s/cat :id string? :coordinates ::coordinates)
  :ret map?
  :fn (s/and
       #(= (-> % :ret keys count) 1)
       #(= (-> % :ret vals count) 1)
       #(= (-> % :args :id) (-> % :ret keys first))
       #(= (-> % :args :coordinates) (-> % :ret vals first :coordinates))))

; ---------------------------------------------------------------------------- ;

(defn intermediate-stations
  "Create a new station between every existent station in a vector"
  ([stations]
   (intermediate-stations stations []))
  ([stations new-stations]
   (if (empty? (rest stations))
     (conj new-stations (first stations))
     (recur (rest stations)
            (into new-stations [(first stations) "x"])))))
