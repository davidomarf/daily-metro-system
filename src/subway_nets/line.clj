(ns subway-nets.line
  "Provides the functions to operate over or with subway lines"
  (:require [subway-nets.math :as math]
            [subway-nets.station :as sn-s]
            [clojure.test.check.generators :as gen]
            [clojure.spec.alpha :as s]))

; ------------------------------- Create a line ------------------------------ ;

(defn subway-line
  "Build a subway line map"
  ([id]
   (subway-line id []))
  ([id stations]
   {:id id :start (first stations) :end (last stations) :stations stations}))

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
              (-> % :ret :stations))))

(defn add-station-end
  "Takes a line map and inserts a new station at the end of :stations line"
  [line station]
  (assoc (assoc line
                :stations
                (into (:stations line) [station]))
         :end station))

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
  (assoc (assoc line
                :stations
                (into [station] (:stations line)))
         :start station))

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
  ([line angle]
   (extend-terminals line angle 20))
  ([line angle limit]
   (if (<= limit 0)
     nil
     (let [size (count (:stations line))]
       (if (>= size limit)
         line
         (let [start-line
               (add-station-start line
                                  (sn-s/subway-station (str "ln-st" size)
                                                       {:x 10 :y 10}
                                                       angle))]
           (if (= (inc size) limit)
             start-line
           (recur (add-station-end start-line
                                   (sn-s/subway-station (str "ln-st" (inc size))
                                                        {:x 10 :y 10}
                                                        angle))
                  angle
                  limit))))))))

    ;; => {:end {:angle 0.8959874902591358, :coordinates {:x 680, :y 355}, :id "st-0"},
    ;;     :id "ln-0",
    ;;     :start {:angle 0.8959874902591358, :coordinates {:x 680, :y 355}, :id "st-0"},
    ;;     :stations [{:angle 0.8959874902591358, :coordinates {:x 680, :y 355}, :id "st-0"}]}


(s/fdef extend-terminals
  :args (s/cat
         :line ::subway-line)
  :ret ::subway-line)



; ------------------------------ Generate a line ----------------------------- ;


(defn g-line
  "Function to generate a new line for the network @net with id @line-id and
    using @station as target for the new line"
  ([net line-id station]
   (let [line (subway-line line-id [station])]
     (extend-terminals line (:angle station) 5))))


(comment
  (do
    (refer 'subway-nets.line)

    ;; Start working with an empty network. Values are hardcoded.
    ;; To check actual net-generators go to subway-nets.network.
    (def empty-net {:id "empty", :lines []})
    
     ;; Heaviest station. This is the target for the new line
    (def h-en {:angle 0.8959874902591358, :coordinates {:x 680, :y 355}, :id "st-0"})
    
    ;; Generate a line 
    ;; [net line-id station]
    (g-line empty-net "ln-0" h-en)
    ;; => {:end {:angle 0.8959874902591358, :coordinates {:x 10, :y 10}, :id "ln-st4"},
    ;;     :id "ln-0",
    ;;     :start {:angle 0.8959874902591358, :coordinates {:x 10, :y 10}, :id "ln-st3"},
    ;;     :stations [{:angle 0.8959874902591358, :coordinates {:x 10, :y 10}, :id "ln-st3"}
    ;;                {:angle 0.8959874902591358, :coordinates {:x 10, :y 10}, :id "ln-st1"}
    ;;                {:angle 0.8959874902591358, :coordinates {:x 680, :y 355}, :id "st-0"}
    ;;                {:angle 0.8959874902591358, :coordinates {:x 10, :y 10}, :id "ln-st2"}
    ;;                {:angle 0.8959874902591358, :coordinates {:x 10, :y 10}, :id "ln-st4"}]}
    )
  )