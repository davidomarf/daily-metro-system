(ns subway-nets.line
  "Provides the functions to operate over or with subway lines"
  (:require [subway-nets.math :as math]
            [subway-nets.station :as sn-s]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]))

; ------------------------------- Create a line ------------------------------ ;

(defn subway-line
  "Build a subway line map"
  [id stations]
  {:id id :start (first stations) :end (last stations) :stations stations})

(s/fdef subway-line
  :args (s/and (s/cat :id string? :stations list?)
               #(not (zero? (count (:stations %))))
               #(not (empty? (:id %))))
  :ret map?
  :fn (s/and #(>= (-> % :ret :stations count) 1)
             #(= (-> % :ret :start) (-> % :ret :stations first))
             #(= (-> % :ret :end) (-> % :ret :stations last))))

(def gen-subway-line
  "Generator for subway lines"
  (gen/fmap #(subway-line (first %) (second %))
            (gen/tuple gen/string-alphanumeric (gen/vector gen/string-alphanumeric))))

(s/def ::subway-line
  (s/with-gen
    (s/and 
     #(>= (-> % :stations count) 2))
    (constantly gen-subway-line)))

; --------------------------- Add stations to lines -------------------------- ;

(defn add-station-middle
  "Takes a subway line and inserts the new station in the (floored) middle spot"
  [line station]  
  (let [[before after]
        (split-at (int (/ (count (:stations line)) 2))
                  (:stations line))]
    (assoc line :stations (vec (concat before [station] after)))))

(s/fdef add-station-middle
  :args (s/cat
         :line ::subway-line
         :id string?)
  :ret ::subway-line
  :fn (s/and
       ;; id of line and ret are equal
       #(= (-> % :args :line :id)
           (-> % :ret :id))
       ;; size of ret is size of line + 1
       #(= (-> % :args :line :stations count inc)
           (-> % :ret :stations count))
       ;; line and ret share the same start station
       #(= (-> % :args :line :start)
           (-> % :ret :start))
       ;; line and ret share the same end station
       #(= (-> % :args :line :end)
           (-> % :ret :end))
       ;; stations of ret contain the id of station
       #(some #{(-> % :args :id)}
              (-> % :ret :stations))
       ))

(defn add-station-end
  "Takes a line map and inserts a new station at the end of :stations line"
  [line station]
  (assoc (assoc line :stations (into (:stations line) [station])) :end station))

(s/fdef add-station-end
  :args (s/cat
         :line ::subway-line
         :id string?)
  :ret ::subway-line
  :fn (s/and
       ;; id of line and ret are equal
       #(= (-> % :args :line :id)
           (-> % :ret :id))
       ;; size of ret is size of line + 1
       #(= (-> % :args :line :stations count inc)
           (-> % :ret :stations count))
       ;; line and ret share the same start station
       #(= (-> % :args :line :start)
           (-> % :ret :start))
       ;; station and ret :end are the same station
       #(= (-> % :args :station)
           (-> % :ret :end :id))))


(defn add-station-start
  "Takes a line map and inserts a new station at the start of :stations line"
  [line station]
  (assoc (assoc line :stations (into [station] (:stations line))) :start station))

(s/fdef add-station-start
  :args (s/cat
         :line ::subway-line
         :station string?)
  :ret ::subway-line
  :fn (s/and
       ;; id of line and ret are equal
       #(= (-> % :args :line :id)
           (-> % :ret :id))
       ;; size of ret is size of line + 1
       #(= (-> % :args :line :stations count inc)
           (-> % :ret :stations count))
       ;; id and ret :end are the same station
       #(= (-> % :args :line :end)
           (-> % :ret :end))
        ;; station and ret :start are the same station
       #(= (-> % :args :station)
           (-> % :ret :start))))


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

(s/fdef extend-terminals
  :args (s/cat 
         :line ::subway-line)
  :ret ::subway-line)