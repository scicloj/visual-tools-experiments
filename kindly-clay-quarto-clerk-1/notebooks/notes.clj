;; # visual-tools meeting 17
;;
;; Kindly & Clay status update
;;
;; 2023-04-01

;; # background

;; ## Your namespace as a notebook

;; ## Different tools

;; * Oz
;; * Notespace (unmaintained)
;; * Clerk
;; * Clay
;; * Calva Notebooks

;; ## Could things be (partially) compatible?


;; # Kindly

;; * A convention for the basic needs
;; * customizable
;; * sensible defaults

;; ## Kindly support
;; * Clay: HTML + Scittle
;; * Clay: [Quarto](https://quarto.org/) + Scittle
;; * Clerk (through kind-clerk)
;; * soon: Portal
;; * soon: Calva Notebooks

;; ## Example use case
;; * Two developers are collaborating on documenting a library.
;; * They use Kindly-based namespaces.
;; * One of the devs uses Clerk for the workflow.
;; * The other dev prefers to use Clay.
;; * They explore some values using Portal.
;; * They render the docs using Clay+Quarto.
;; * They encourage their users to try the docs using Calva Notebooks + Portal.

;; # Clay
;; A very basic tool for datavis & literate programming


;; # examples

;; ## setup

(ns notes
  (:require [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [aerial.hanami.common :as hc]
            [scicloj.noj.v1.vis :as noj.vis]
            [scicloj.kindly.v3.kind :as kind]
            [scicloj.clay.v2.util.image :as clay.image]
            [tech.v3.datatype.functional :as fun]
            [clojure.string :as string]
            [scicloj.kind-clerk.api :as kind-clerk]
            [scicloj.clay.v2.api :as clay]))

(kind-clerk/setup!)


;; ## some data

(def data
  (-> "data/iris.csv.gz"
      (tc/dataset
       {:key-fn (fn [colname]
                  (-> colname
                      (string/replace #"\." "-")
                      string/lower-case
                      keyword))})
      (tc/add-column :petal-size
                     #(fun/sqrt
                       (fun/+ (fun/sq (:petal-width %))
                              (fun/sq (:petal-length %)))))))

;; ## printing datasets

(-> data
    (tc/random 5))

;; ## table view

(-> data
    kind/table)

;; ## plotting


;; Tablecloth, Hanami, Kindly

(-> data
    (tc/rows :as-maps)
    (#(hc/xform ht/point-chart
                :DATA %
                :X "sepal-width"
                :Y "sepal-length"
                :COLOR "species"
                :SIZE "petal-size"))
    (assoc-in [:encoding :size :type]
              "quantitative")
    kind/vega-lite)

;; ## easier plotting

;; Tablecloth, Hanami, Kindly, Noj

(-> data
    (noj.vis/hanami-plot ht/point-chart
                         :X "sepal-width"
                         :Y "sepal-length"
                         :COLOR "species"
                         :SIZE "petal-size")
    (assoc-in [:encoding :size :type]
              "quantitative"))

;; ## MathBox.cljs

;; inspired by [the official MathBox.cljs tutorial](https://mathbox.mentat.org/)
(kind/hiccup
 '[(let [Data (fn []
                [mathbox.primitives/Interval
                 {:expr (fn [emit x _i t]
                          (emit x (Math/sin (+ x t))))
                  :width 64
                  :channels 2}])
         Curve (fn []
                 [:<>
                  [Data]
                  [mathbox.primitives/Line {:width 5
                                            :color "#3090FF"}]])
         Main (fn []
                [mathbox.core/MathBox
                 {:container {:style {:height "400px" :width "100%"}}
                  :focus 3}
                 [mathbox.primitives/Camera {:position [0 0 3]
                                             :proxy true}]
                 [mathbox.primitives/Cartesian
                  {:range [[-2 2] [-1 1]]
                   :scale [2 1]}
                  [mathbox.primitives/Axis {:axis 1 :width 3 :color "black"}]
                  [mathbox.primitives/Axis {:axis 2 :width 3 :color "black"}]
                  [mathbox.primitives/Grid {:width 2 :divideX 20 :divideY 10}]
                  [Curve]]])
         *state (r/atom {:open? false})]
     (fn []
       [:div
        [:button {:on-click #(swap! *state update :open? not)}
         (if (:open? @*state)
           "close"
           "open")]
        (when (:open? @*state)
          [Main])]))])

;; # Reproducible backend computation

;; ## Computing something

(defn slow [value]
  (Thread/sleep 1000)
  (* 1000 value))



(kind/hiccup
 '[(fn []
     (let [*state (r/atom
                   {:value 50})]
       (fn []
         (let [value (:value @*state)]
           [:div
            [:input {:type "range"
                     :value (:value @*state)
                     :min 0
                     :max 100
                     :style {:width "100%"}
                     :on-change (fn [e]
                                  (let [new-value (js/parseInt (.. e -target -value))]
                                    (swap! *state assoc :value new-value)))}]
            [:p value]
            [clay/compute `(slow ~value)]]))))])

;; ## Populating the cache

(defn slow1 [value]
  (Thread/sleep 1000)
  (* 1000 value))

(kind/hiccup
 (list 'clay/reset-cache!
       (->> (range 100)
            (map (fn [value]
                   [`'(slow1 ~value)
                    (* 1000 value)]))
            (into {}))))

(kind/hiccup
 '[(fn []
     (let [*state (r/atom
                   {:value 50})]
       (fn []
         (let [value (:value @*state)]
           [:div
            [:input {:type "range"
                     :value (:value @*state)
                     :min 0
                     :max 100
                     :style {:width "100%"}
                     :on-change (fn [e]
                                  (let [new-value (js/parseInt (.. e -target -value))]
                                    (swap! *state assoc :value new-value)))}]
            [:p value]
            [clay/compute `(slow1 ~value)]]))))])





;; # Near-term plans
;; * Create an easy entry point demo for newcomers
;; * Be use-case driven
;; * Look into documenting lobraries
;; * Work on Kindly adapters: Portal, Calva Notebooks
;; * Embed Portal viewers in Quarto, etc.

:bye
