;; # Compatibility experiment: Portal-Clerk-Kindly-nREPL

;; This small experiment, [portal-clerk-kindly-nrepl](https://github.com/scicloj/visual-tools-experiments/tree/main/portal-clerk-kindly-nrepl), explores one possible path towards Clojure visual tools compatibility. It is part of our explorations at the [visual-tools group](https://scicloj.github.io/docs/community/groups/visual-tools/).

;; Here, we demonstrate writing one namespace that can be rendered at both [Portal](https://github.com/djblue/portal) (for dynamic data navigation and visualization) and [Clerk](https://github.com/nextjournal/clerk) (for documentation through literate programming).

;; The solution is based on parts of [Notespace](github.com/scicloj/notespace/) and [Kindly](github.com/scicloj/kindly). For clarity, we avoid those dependencies here and include all relevant functionality here in the repo.

;; Kindly is used to specify the kinds of things, where kinds determine their rendering behaviour in different tools.

;; Another aspect explored in this experiment is interacting with Portal through nREPL middleware listening to the user evaluations of code in the IDE, thus avoiding the need to send values through `tap>`. For a more basic experiment with this aspect, see the [portal-nrepl-1 experiment](https://github.com/scicloj/visual-tools-experiments/tree/main/portal-nrepl-1).

;; ## Setup

(ns example
  (:require [portal-clerk-kindly-nrepl-1.pipeline :as pipeline]
            [portal-clerk-kindly-nrepl-1.kindly.api :as kindly]
            [portal-clerk-kindly-nrepl-1.kindly.kindness :as kindness]
            [portal-clerk-kindly-nrepl-1.kindly.kind :as kind]
            [portal-clerk-kindly-nrepl-1.view.clerk :as view.clerk]
            [clojure.test :refer [is deftest]]
            [nextjournal.clerk :as clerk]))

;; Start the event pipeline listening to nREPL and sending values to be visualized at Portal:
(pipeline/start)

;; In case you need to restart for troubleshooting:
(comment
  (pipeline/restart))

;; Setup the ways things would be viewed in Clerk:
(view.clerk/setup!)

;; Some useful Clerk commands:
(comment
  (clerk/serve! {})
  (clerk/clear-cache!)
  ,)

;; ## Specifying kinds

;; The kind of a value says how it should be rendered. It can be specified in different ways. Ideally, we want this specification to apply to all relevant visual tools (e.g., Portal, Clerk).

;; ### By metadata attached to the value
(-> [:small "hello"]
    (kindly/consider kind/hiccup))

;; ### By metadata attached to the code
;; (does not work in Clerk)
^:kind/hiccup
[:small "hello"]

;; ### By protocol

;; The following example is rather artificial, but a similar approach is quite valuable for some types (e.g., BufferedImage).

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

;; ## Delays

(delay (Thread/sleep 2000)
       9)

(delay
  (Thread/sleep 2000)
  (-> [:h1 "a"]
      (kindly/consider kind/hiccup)))

(defn vega-lite-spec [n]
  (-> {:data {:values
              (->> (repeatedly n #(- (rand) 0.5))
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
      (kindly/consider kind/vega-lite)))

(-> 200
    vega-lite-spec)

(-> (->> [10 100 1000]
         (map (fn [n]
                [:div
                 [:h1 (str "n=" n)]
                 (vega-lite-spec n)]))
         (into [:div]))
    (kindly/consider kind/hiccup))




(kindly/check 9)

(kindly/check (-> 2 (+ 3) (= 5)))
(kindly/check (-> 2 (+ 3) (= 4)))


(-> 2
    (+ 3)
    (kindly/check < 100))

(-> 2
    (+ 3)
    (kindly/check > 100))

:bye
