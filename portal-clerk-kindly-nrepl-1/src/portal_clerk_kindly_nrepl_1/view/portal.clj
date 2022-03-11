(ns portal-clerk-kindly-nrepl-1.view.portal
  (:require [portal-clerk-kindly-nrepl-1.kindly.api :as kindly]
            [portal-clerk-kindly-nrepl-1.walk :as walk]))

(kindly/define-kind-behaviour! :kind/hiccup
  {:portal.viewer (fn [v]
                    [:portal.viewer/hiccup v])})

(kindly/define-kind-behaviour! :kind/vega-lite
  {:portal.viewer (fn [v]
                    [:portal.viewer/vega-lite v])})

(defn maybe-apply-viewer [value kind]
  (if-let [viewer (-> kind kindly/kind->behaviour :portal.viewer)]
    (viewer value)
    value))

(defn prepare [value code]
  (let [v (if-let [code-kind (kindly/code->kind code)]
            (maybe-apply-viewer value code-kind)
            (->> value
                 (walk/postwalk
                  (fn [subvalue]
                    (->> subvalue
                         kindly/kind
                         (maybe-apply-viewer subvalue))))))]
    (if (and (vector? v)
             (-> v first keyword?)
             (-> v first namespace (= "portal.viewer")))
      (-> v
          (vary-meta assoc
                     :portal.viewer/default
                     :portal.viewer/hiccup))
      v)))
