(ns example
  (:require [portal-clerk-kindly-nrepl-1.pipeline :as pipeline]
            [portal-clerk-kindly-nrepl-1.kindly.api :as kindly]
            [portal-clerk-kindly-nrepl-1.kindly.kindness :as kindness]
            [portal-clerk-kindly-nrepl-1.kindly.kind :as kind]
            [portal-clerk-kindly-nrepl-1.view.clerk :as view.clerk]
            [nextjournal.clerk :as clerk]))

(pipeline/start)

(view.clerk/setup!)

(comment
  (pipeline/restart)
  (clerk/serve! {})
  (clerk/clear-cache!)
  ,)

(+ 1 2)

(delay (Thread/sleep 2000)
       9)

^:kind/hiccup
[:h1 "a"]

(-> [:h1 "a"]
    (kindly/consider kind/hiccup))

(delay
  (Thread/sleep 2000)
  (-> [:h1 "a"]
      (kindly/consider kind/hiccup)))

(-> {:data {:values
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
      :fill {:field "z", :type "nominal"}}}
    (kindly/consider kind/vega-lite))


(kindly/define-kind-behaviour!
  :kind/bigbigtext
  {:portal.viewer (fn [v]
                    [:portal.viewer/hiccup [:big [:big (:text v)]]])
   :clerk.viewer (fn [v]
                   (clerk/html [:big [:big (:text v)]]))})

(defrecord BigBigText [text]
  portal-clerk-kindly-nrepl-1.kindly.kindness/Kindness
  (portal-clerk-kindly-nrepl-1.kindly.kindness/kind
    [this]
    :kind/bigbigtext))

(->BigBigText "hi!")

:bye
