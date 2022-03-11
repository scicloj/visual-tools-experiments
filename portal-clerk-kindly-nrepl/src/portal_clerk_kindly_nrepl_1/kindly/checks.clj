(ns portal-clerk-kindly-nrepl-1.kindly.checks
  (:require [portal-clerk-kindly-nrepl-1.kindly.behaviours :as behaviours]
            [nextjournal.clerk :as clerk]))

(defn bool->symbol [bool]
  [:big [:big (if bool
                [:big {:style {:color "darkgreen"}}
                 "✓"]
                [:big {:style {:color "darkred"}}
                 "❌"])]])

(defn test-boolean->hiccup [bool]
  [:div
   (bool->symbol bool)
   (str "   " bool)])

(behaviours/define-kind-behaviour!
  :kind/check
  {:portal.viewer (fn [v]
                    [:portal.viewer/hiccup (-> v
                                               :result
                                               test-boolean->hiccup)])
   :clerk.viewer (fn [v]
                   (-> v
                       :result
                       test-boolean->hiccup
                       clerk/html))})

(defn check [value & predicate-and-args]
  (-> {:result (-> (if predicate-and-args
                     (apply (first predicate-and-args)
                            value
                            (rest predicate-and-args))
                     value)
                   (if true false))}
      (vary-meta assoc :kindly/kind :kind/check)))
