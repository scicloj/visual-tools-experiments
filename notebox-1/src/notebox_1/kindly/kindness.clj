(ns notebox-1.kindly.kindness)


(defprotocol Kindness
  (kind [this]))

(extend-protocol Kindness
  nil
  (kind [this]
    nil)
  Object
  (kind [this]
    nil))
