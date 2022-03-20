(ns notebox-1.kindly.api
  (:require [notebox-1.kindly.kind :as kind]
            [notebox-1.kindly.kindness :as kindness]
            [notebox-1.kindly.behaviours :as behaviours]
            [notebox-1.kindly.checks :as checks]))

(defn define-kind-behaviour! [kind behaviour]
  (behaviours/define-kind-behaviour! kind behaviour))

(defn kind->behaviour [kind]
  (behaviours/kind->behaviour kind))

(defn consider [value kind]
  (vary-meta value assoc :kindly/kind kind))

(defn kinds-set []
  (set (keys @behaviours/*kind->behaviour)))

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

(defn check [value & predicate-and-args]
  (apply checks/check value predicate-and-args))
