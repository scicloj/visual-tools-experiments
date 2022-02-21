(ns example
  (:require [portal-nrepl-1.pipeline :as pipeline]))

(pipeline/start)

(+ 1 2)

[:portal.viewer/hiccup [:h1 "hi!"]]

[:portal.viewer/code "{:x 9 :y [(+ 1 2)]}"]
