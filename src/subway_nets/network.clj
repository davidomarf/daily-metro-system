(ns subway-nets.network
  "Provides the functions to operate over or with the subway network"
  (:require [subway-nets.math :as math]
            [subway-nets.station :as sn-s]
            [subway-nets.line :as sn-l]))

(defn subway-network
  "Create a subway network map receiving [id] or [id lines]"
  ([id]
   (subway-network id []))
  ([id lines]
   {:id id :lines lines}))

(defn all-stations
  "Get a vector with all the stations in the network"
  [net]
  ;; Create a vector by iterating over each station in each line
  (apply into
         (map #(-> % :stations)
              (-> net :lines))))

(defn stations->points
  "Convert a vector of ::stations into a vector of [x y {station}]"
  [stations]
  (vec (map #(let [coord (-> % :coordinates)]
               [(:x coord) (:y coord) %]) stations)))

(defn heaviest-station
  "Get the station with the highest usage load. Returns [x y {station} occurences]"
  [net]
  (if (= (-> net :lines count) 0)
    ;; Use a default station for when there are none in the net
    (let [station (sn-s/subway-station "st-0" {:x (/ 1360 2) :y (/ 710 2)})]
      [[(-> station :coordinates :x) 
       (-> station :coordinates :y)
       station]
       1000])

    ;; Compute the loads for the stations
    (let [point-stations (stations->points (all-stations net))
          loads (sn-s/stations-load point-stations)
          freq (frequencies (map first loads))]
      (apply max-key val freq))))

(defn add-line
  "Appends the new line at the end of (net :lines)"
  [net line]
  (assoc net :lines (conj (:lines net) line)))

(defn g-network
  "Generate a whole metro network.
   This is the main function and should be called only once"
  ([limit]
   (g-network limit 0 (subway-network "network")))
  ([limit line-num net]
   ;; Return the final network once it has enough lines
   (if (>= line-num limit)
     net
     ;; Otherwise, find a pivot and create a new line using it
     (let [pivot (last (first (heaviest-station net)))
           new-line (sn-l/g-line net (str "ln-" line-num) pivot)]
       (recur limit
              (inc line-num)
              (add-line net new-line))))))

(comment
  (do
    (refer 'subway-nets.network)

    ;; Create an empty network    
    (def empty (subway-network "empty"))

    ;; If all the points inside the city had to compute the closest of all the
    ;; stations in the network, the **heaviest station** is the one that is the
    ;; closest for more points.
    
    ;; Get the heaviest station of the network.
    ;; The angle indicates the direction of the new line, and it's a number between
    ;; [0, 1] that will later multiply (2 * PI).
    ;; When the network is empty, coordinates are the middle of the map, and the
    ;; angle is a random number.
    (def pivot (heaviest-station empty))
    ;; => #'subway-nets.network/pivot
    

    ;; Create a new generated line using previously generated pivot and empty net
    (def first-line (sn-l/g-line empty
                                 "ln-0"
                                 (last (first pivot))))
    
    ;; Test adding first-line to an empty network
    (def one-line (add-line empty first-line))
    one-line

    ;; The main function. Generate a network with n lines. In this case n = 5;
    ;;FIX
    (g-network 5)
    
    (heaviest-station empty)
    ;; => {:angle 0.5059656302411475, :coordinates {:x 680, :y 355}, :id "st-0"}
    
    ; --------------------------------- TODO --------------------------------- ;
    
    ;; - Proper function to obtain the pivot. Use Voronoi diagrams.
    ;; - Determine new angle direction for newly created lines.
    ;; - Obtain angle for existing lines (and stations)
    )
  )