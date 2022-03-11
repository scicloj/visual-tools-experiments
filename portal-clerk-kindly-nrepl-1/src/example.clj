;; # Compatibility experiment: Portal-Clerk-Kindly-nREPL

;; This small experiment, [portal-clerk-kindly-nrepl-1](https://github.com/scicloj/visual-tools-experiments/tree/main/portal-clerk-kindly-nrepl-1), explores one possible path towards compatibility between different Clojure visual tools.  It is part of our explorations at the [visual-tools group](https://scicloj.github.io/docs/community/groups/visual-tools/).

;; ## The problem

;; Clojure has [an amazing diversity](https://scicloj.github.io/docs/resources/libs/#visual-tools-literate-programming-and-data-visualization) of tools for data visualization and literate programming.

;; This is wonderful, but brings up a problem: each tool has its own conventions of specifying how things should be shown visually.

;; See, for example, the following notebooks, each created in a different format (though many of them are written by the same person).
;; - [Anglican tutorials](https://probprog.github.io/anglican/examples/index.html) ([source](https://bitbucket.org/probprog/anglican-examples/src/master/worksheets/)) - written in [Gorilla REPL](https://github.com/JonyEpsilon/gorilla-repl)
;; - [thi-ng/geom viz examples](https://github.com/thi-ng/geom/blob/feature/no-org/org/examples/viz/demos.org)  ([source](https://raw.githubusercontent.com/thi-ng/geom/feature/no-org/org/examples/viz/demos.org)) - written in [Org-babel-clojure](https://orgmode.org/worg/org-contrib/babel/languages/ob-doc-clojure.html)
;; - [Clojure2d docs](https://github.com/Clojure2D/clojure2d#Usage) ([source1](https://github.com/Clojure2D/clojure2d/blob/master/src/clojure2d), [source2](https://github.com/Clojure2D/clojure2d/blob/master/metadoc/clojure2d/)) - written in [Codox](https://github.com/weavejester/codox) and [Metadoc](https://github.com/generateme/metadoc)
;; - [Tablecloth API docs](https://scicloj.github.io/tablecloth/index.html) ([source](https://github.com/scicloj/tablecloth/blob/master/docs/index.Rmd)) - written in [rmarkdown-clojure](https://github.com/genmeblog/rmarkdown-clojure)
;; - [R interop ClojisR examples](https://github.com/scicloj/clojisr-examples) ([source](https://github.com/scicloj/clojisr-examples/tree/master/src/clojisr_examples)) - written in [Notespace v2](https://github.com/scicloj/notespace/blob/master/doc/v2.md)
;; - [Bayesian optimization tutorial](https://nextjournal.com/generateme/bayesian-optimization) ([source](https://nextjournal.com/generateme/bayesian-optimization)) - written in [Nextjournal](https://nextjournal.com/)
;; - [scicloj.ml tutorials](https://github.com/scicloj/scicloj.ml-tutorials#tutorials-for-sciclojml) ([source](https://github.com/scicloj/scicloj.ml-tutorials/tree/main/src/scicloj/ml)) - written in [Notespace v3](https://github.com/scicloj/notespace/blob/master/doc/v3.md)
;; - [Clojure2d color tutorial](https://clojure2d.github.io/clojure2d/docs/notebooks/index.html#/notebooks/color.clj) ([source](https://github.com/Clojure2D/clojure2d/blob/master/notebooks/color.clj)) - written in [Clerk](https://github.com/nextjournal/)

;; In all of these tutorials, a user willing to use the code examples would need to adapt the code to work with their tool of choice.

;; Maybe we could hope for a simpler situation, where the Clojure collection of docs, tutorials, and blog posts would keep growing, and the landscape of visual tools would keep growing too, but without facing such conflicts.


;; ## A solution? (proof of concept)

;; Here, we demonstrate a thin layer proposing a solution. It is certainly not a complete solution, but hopefully, it can provoke a discussion.

;; We demonstrate writing one namespace that - with just a little extra setup - can be rendered at both [Portal](https://github.com/djblue/portal) (for dynamic data navigation and visualization) and [Clerk](https://github.com/nextjournal/clerk) (for documentation through literate programming).

;; The solution is based on parts of [Notespace](https://github.com/scicloj/notespace/) and [Kindly](https://github.com/scicloj/kindly). For clarity, we avoid those dependencies, and include a self-contained version of the relevant functionality here in the repo. We also adapt the kindly API a little bit, to fit the current intentions.

;; Kindly is used to specify the kinds of things, where kinds determine their visual behaviour in different tools.

;; Another aspect explored in this experiment is interacting with Portal through [nREPL](https://nrepl.org) middleware listening to the user evaluations of code in the IDE, thus avoiding the need to send values through `tap>`. For a more basic experiment with this aspect, see the [portal-nrepl-1 experiment](https://github.com/scicloj/visual-tools-experiments/tree/main/portal-nrepl-1).

;; ## Setup

(ns example
  (:require [portal-clerk-kindly-nrepl-1.pipeline :as pipeline]
            [portal-clerk-kindly-nrepl-1.kindly.api :as kindly]
            [portal-clerk-kindly-nrepl-1.kindly.kindness :as kindness]
            [portal-clerk-kindly-nrepl-1.kindly.kind :as kind]
            [portal-clerk-kindly-nrepl-1.view.clerk :as view.clerk]
            [clojure.test :refer [is deftest]]
            [nextjournal.clerk :as clerk]))

;;; ### Portal

;; Let us start the event pipeline that would pass code evaluations to Portal.
;; This will also open the Portal view.
(pipeline/start)

;; Now, evaluating code in your REPL would send the values to be visualized in Portal.

;; For this to work, you need to use an nREPL-based Clojure environment (e.g., CIDER, Calva). You also need to run your REPL with the `-M:nrepl` [alias](https://github.com/scicloj/visual-tools-experiments/blob/main/portal-clerk-kindly-nrepl-1/deps.edn#L5).

;; For troubleshootimg, it might be useful to restart the pipeline:
(comment
  (pipeline/restart))

;;; ### Clerk

;; Let us set things up so that things will be rendered as we like.
(view.clerk/setup!)

;; Now you can use Clerk as you like.

;; Some useful Clerk commands for the Clerk workflow:
(comment
  (clerk/serve! {})
  (clerk/clear-cache!)
  (clerk/build-static-app! {:paths ["src/example.clj"]})
  ,)

;; ## Specifying kinds

;; The kind of a value determintes how it should be rendered in different tools. It can be specified in different ways.

;; ### By metadata attached to the value

;; The `kindly/consider` function attaches metadata to a value, that specifies its `kind`. For example:

(-> [:small "hello"]
    (kindly/consider kind/hiccup))

;; ### By metadata attached to the code

;; One can also attach metadata to the source code. The following does not work correctly with Clerk yet, but it does with Portal.

^:kind/hiccup
[:small "hello"]

;; ### By protocol

;; Sometimes, it is handy to dispatch on types to determine the kind of desired rendering. This can be done using the `Kindness` protocal. Foe example, let us define custom rendering for Clojure Vars.

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

;; Let us try this with a Var from the Clojure core library:

#'reduce

;; ## Delays

;; Sometimes, it is handy to wrap slow code in delays, if we do not want it to run every time we load the whole code buffer.

;; The current setup makes sure to dereference delays when rendering in either Portal or Clerk.

(delay (Thread/sleep 500)
       9)

(delay
  (Thread/sleep 500)
  (-> [:small "hello"]
      (kindly/consider kind/hiccup)))




;; ## Nesting


;; Both Portal and Clerk support nesting of viewer types. So, for example, we can nest specs of `kind/vega-lite` inside one spec of `kind/hiccup`.

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








;; ## Unit tests

;; The `kindly/check` function allows to test some assumptions and return a value of kind `kind/check`, that is rendered appropriately.

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

;; A similar solution for literate unit tests (or rather, testable documents) has been used in ClojisR -- see for example [this tutorial](https://scicloj.github.io/clojisr/doc/clojisr/v1/tutorial-test) ([source](https://github.com/scicloj/clojisr/blob/master/test/clojisr/v1/tutorial_test.clj)).

;; One idea we are considering is integrating it with a so-called doctest solution, so that standard `clojure.test` unit-tests would be automatically generated from this code.
