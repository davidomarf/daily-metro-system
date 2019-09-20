(ns subway-nets.station
  "Provides the functions to operate over or with subway stations"
  (:require [subway-nets.math :as math]))

; ----------------------------- Create a station ----------------------------- ;

(defn subway-station
  "Build subway station map"
  [id coordinates]
  {:id id :coordinates coordinates})

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
