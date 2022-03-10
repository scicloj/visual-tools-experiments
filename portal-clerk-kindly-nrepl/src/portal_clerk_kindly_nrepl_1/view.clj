(ns portal-clerk-kindly-nrepl-1.view
  (:require [portal.api :as portal]
            [portal-clerk-kindly-nrepl-1.view.portal :as view.portal]))

(defn open []
  (portal/open))

(defn close []
  (portal/close))

(defn deref-if-needed [v]
  (if (delay? v)
    (let [_ (println "deref ...")
          dv @v
          _ (println "done.")]
      dv)
    v))

(defn show [value code]
  (-> [:div
       [:portal.viewer/code code]
       [:portal.viewer/inspector
        (-> value
            deref-if-needed
            (view.portal/prepare code))]]
      (with-meta
        {:portal.viewer/default :portal.viewer/hiccup})
      portal/submit))
