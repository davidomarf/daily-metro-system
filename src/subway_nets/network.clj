(ns subway-nets.network
  "Provides the functions to operate over or with the subway network"
  (:require [subway-nets.math :as math]
            [subway-nets.station :as sn-s]
            [subway-nets.line :as sn-l]
            [voronoi-diagram.core :as voronoi]))


(defn subway-network
  "Create a subway network map receiving [id] or [id lines]"
  ([id]
   (subway-network id []))
  ([id lines]
   {:id id :lines lines}))

(defn all-stations
  "Get a vector with all the stations in the network"
  [net]
  (map #(-> % :stations) (-> net :lines vals)))

(defn heaviest-station
  "Get the station with the highest usage load" 
  [net]
  (if (= (-> net :lines count) 0)
    (sn-s/subway-station "st-0" {:x (/ 1360 2) :y (/ 710 2)})
    (let [stations (all-stations net)]
      (first stations))))

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
   (if (>= line-num limit)
     net
     (let [station (heaviest-station net)]
       (recur limit
              (inc line-num)
              (add-line net
                        (sn-l/g-line net
                                     (str "ln-" line-num)
                                     station)))))))

(comment
  (do
    (refer 'subway-nets.network)

    ;; Create an empty network    
    (def empty (subway-network "empty"))
    empty
    ;; => {:id "empty", :lines []}
    
    ;; If all the points inside the city had to compute the closest of all the
    ;; stations in the network, the **heaviest station** is the one that is the
    ;; closest for more points.
    
    ;; Get the heaviest station of the network.
    ;; The angle indicates the direction of the new line, and it's a number between
    ;; [0, 1] that will later multiply (2 * PI).
    ;; When the network is empty, coordinates are the middle of the map, and the
    ;; angle is a random number.
    (def pivot (heaviest-station empty))
    pivot
    ;; => {:angle 0.026550332688665135, :coordinates {:x 680, :y 355}, :id "st-0"}
    
    ;; Create a new generated line using the heaviest point as the pivot
    (def first-line (sn-l/g-line empty
                                 "ln-0"
                                 pivot))
    first-line
    ;; => {:end {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st4"},
    ;;     :id "ln-0",
    ;;     :start {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st3"},
    ;;     :stations [{:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st3"}
    ;;                {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st1"}
    ;;                {:angle 0.026550332688665135, :coordinates {:x 680, :y 355}, :id "st-0"}
    ;;                {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st2"}
    ;;                {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st4"}]}
    

    (add-line empty first-line)
    ;; => {:id "empty",
    ;;     :lines [{:end {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st4"},
    ;;              :id "ln-0",
    ;;              :start {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st3"},
    ;;              :stations [{:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st3"}
    ;;                         {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st1"}
    ;;                         {:angle 0.026550332688665135, :coordinates {:x 680, :y 355}, :id "st-0"}
    ;;                         {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st2"}
    ;;                         {:angle 0.026550332688665135, :coordinates {:x 10, :y 10}, :id "ln-st4"}]}]}
    
    ;; The main function. Generate a network with n lines. In this case n = 5;
    (g-network 5)
    ;;
    )
  )