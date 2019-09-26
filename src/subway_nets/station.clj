(ns subway-nets.station
  "Provides the functions to operate over or with subway stations"
  (:require [subway-nets.math :as math]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as t]))

; --------------------------- Generators for specs --------------------------- ;

(def gen-coordinates
  "Generator for {:x :y} coordinates"
  (gen/hash-map 
   :x (gen/fmap #(Math/abs %) gen/double)
   :y (gen/fmap #(Math/abs %) gen/double)))

(s/def ::coordinates
  (s/with-gen
    (s/and
     #(>= (-> % :x) 0)
     #(>= (-> % :y) 0))
    (constantly gen-coordinates)))

; ----------------------------- Create a station ----------------------------- ;

(defn subway-station
  "Build subway station map"
  ([id coordinates]
   (subway-station id coordinates (rand)))
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

(do
  (def test (subway-station "st-1" {:x 10 :y 10}))
  test
  )

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
