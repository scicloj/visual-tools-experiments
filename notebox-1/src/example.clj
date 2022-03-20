(ns example
  (:require [notebox-1.pipeline :as pipeline]
            [notebox-1.kindly.api :as kindly]
            [notebox-1.kindly.kindness :as kindness]
            [notebox-1.kindly.kind :as kind]
            [notebox-1.view.clerk :as view.clerk]
            [clojure.test :refer [is deftest]]
            [nextjournal.clerk :as clerk]
            [tech.v3.dataset :as ds]))

(pipeline/start)

(comment
  (pipeline/restart))

(view.clerk/setup!)

(comment
  (clerk/serve! {})
  (clerk/show! "src/example.clj")
  (clerk/clear-cache!)
  (clerk/build-static-app! {:paths ["src/example.clj"]}))

(-> [:small "hello"]
    (kindly/consider kind/hiccup))

(extend-protocol kindness/Kindness
  clojure.lang.Var
  (kind [this]
    :kind/var))

(kindly/define-kind-behaviour!
  :kind/var
  {:portal.viewer (fn [v]
                    [:portal.viewer/hiccup
                     [:div
                      [:portal/code (str v)]
                      (when-let [doc (-> v meta :doc)]
                        [:p [:b "doc: "] [:portal/markdown doc]])]])
   :clerk.viewer (fn [v]
                   (clerk/html
                    [:div
                     (clerk/code (str v))
                     (when-let [doc (-> v meta :doc)]
                       [:p [:b "doc: "] (clerk/md doc)])]))})

#'reduce

(delay (Thread/sleep 500)
       9)

(delay
  (Thread/sleep 500)
  (-> [:small "hello"]
      (kindly/consider kind/hiccup)))

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


(-> (->> [10 100 1000]
         (map (fn [n]
                [:div {:style {:width "400px"}}
                 [:h1 (str "n=" n)]
                 (vega-lite-spec n)]))
         (into [:div]))
    (kindly/consider kind/hiccup))


(-> 2
    (+ 3)
    (= 5)
    kindly/check)

(-> 2
    (+ 3)
    (= 4)
    kindly/check)

(-> 2
    (+ 3)
    (kindly/check < 100))

(-> 2
    (+ 3)
    (kindly/check > 100))


^:kind/hide-code
(+ 1 2)




(ds/->dataset {:x (range 99)
               :y (repeatedly 99 rand)})


{:a (ds/->dataset {:x (range 2)
                   :y (repeatedly 2 rand)})}

(import java.awt.image.BufferedImage
        java.awt.Color
        sun.java2d.SunGraphics2D)


(let [bi (BufferedImage. 40 40 BufferedImage/TYPE_INT_RGB)
      g  (-> (.createGraphics ^BufferedImage bi))]
  (.drawLine ^SunGraphics2D g 0 0 40 40)
  bi)
