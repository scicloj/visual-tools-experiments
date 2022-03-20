(ns notebox-1.kindly.behaviours)

(def *kind->behaviour (atom {}))

(defn define-kind-behaviour! [kind behaviour]
  (swap! *kind->behaviour
         update kind
         merge behaviour)
  (intern 'notebox-1.kindly.kind (symbol (name kind)) kind))

(defn kind->behaviour [kind]
  (@*kind->behaviour kind))
