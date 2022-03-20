(ns example
  (:require [notebox-1.pipeline :as pipeline]
            [notebox-1.kindly.api :as kindly]
            [notebox-1.kindly.kindness :as kindness]
            [notebox-1.kindly.kind :as kind]
            [notebox-1.view.clerk :as view.clerk]
            [clojure.test :refer [is deftest]]
            [nextjournal.clerk :as clerk]))

(pipeline/start)

(view.clerk/setup!)

(def vega-lite-spec
  (memoize
   (fn [n]
     (-> {:data {:values
                 (->> (repeatedly n #(- (rand) 0.5))
                      (reductions +)
                      (map-indexed (fn [x y]
                                     {:w (rand-int 9)
                                      :z (rand-int 9)
                                      :x x
                                      :y y})))},
          :mark "point"
          :encoding
          {:size {:field "w" :type "quantitative"}
           :x {:field "x", :type "quantitative"},
           :y {:field "y", :type "quantitative"},
           :fill {:field "z", :type "nominal"}}}
         (kindly/consider kind/vega-lite)))))

(vega-lite-spec 3)


(-> (->> [1 2]
         (map (fn [n]
                [:div {:style {:width "400px"}}
                 [:h1 (str "n=" n)]
                 (vega-lite-spec n)]))
         (into [:div]))
    (kindly/consider kind/hiccup))


:bye

;; (->> doc1 :doc :nextjournal/value :blocks first :nextjournal/value :nextjournal/edn
;;      (spit "/tmp/edn1.edn"))
