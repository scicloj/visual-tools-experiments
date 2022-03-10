(ns portal-clerk-kindly-nrepl-1.kindly.api
  (:require [portal-clerk-kindly-nrepl-1.kindly.kind :as kind]
            [portal-clerk-kindly-nrepl-1.kindly.kindness :as kindness]))

(def *kind->behaviour (atom {}))

(defn define-kind-behaviour! [kind behaviour]
  (swap! *kind->behaviour
         update kind
         merge behaviour)
  (intern 'portal-clerk-kindly-nrepl-1.kindly.kind (symbol (name kind)) kind))

(defn kind->behaviour [kind]
  (@*kind->behaviour kind))


(defn consider [value kind]
  (vary-meta value assoc :kindly/kind kind))



(defn kinds-set []
  (set (keys @*kind->behaviour)))

(defn code->kind [code]
  (when-let [m (some-> code
                       read-string
                       meta)]
    (or (some->> m
                 :tag
                 resolve
                 deref
                 ((kinds-set)))
        (some->> m
                 keys
                 (filter (kinds-set))
                 first))))

(defn value->kind [value]
  (or (-> value
          meta
          :kindly/kind)
      (-> value
          kindness/kind)))

(defn kind
  ([value]
   (kind value nil))
  ([value code]
   (or (-> code
           code->kind)
       (-> value
           value->kind))))
