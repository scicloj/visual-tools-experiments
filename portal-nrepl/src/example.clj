(ns example
  (:require [portal-nrepl-1.pipeline :as pipeline]))

(pipeline/start)

(+ 1 2)

[:portal.viewer/hiccup [:h1 "hi!"]]

[:portal.viewer/code "{:x 9 :y [(+ 1 2)]}"]

[:portal.viewer/tree {:x 9 :y [(+ 1 2)]}]



(defn random-tree [max-depth]
  (if (pos? max-depth)
    [(random-tree (- max-depth (rand-int 2)))
     (random-tree (- max-depth (rand-int 2)))]
    []))

[:portal.viewer/tree (random-tree 2)]



[:h1
 "A plot:"
 [:portal.viewer/vega-lite
  {:data {:values
          (->> (repeatedly 200 #(- (rand) 0.5))
               (reductions +)
               (map-indexed (fn [x y]
                              {:w (rand-int 9)
                               :z (rand-int 9)
                               :x x
                               :y y})))},
   :width 200,
   :height 200,
   :mark "point"
   :encoding
   {:size {:field "w" :type "quantitative"}
    :x {:field "x", :type "quantitative"},
    :y {:field "y", :type "quantitative"},
    :fill {:field "z", :type "nominal"}}}]]
