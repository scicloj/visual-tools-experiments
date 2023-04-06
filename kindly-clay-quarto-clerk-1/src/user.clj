(ns user
  (:require [nextjournal.clerk :as clerk]
            [scicloj.kindly-default.v1.api :as kindly-default]
            [scicloj.clay.v2.api :as clay]))

;; Initialize Kindly's [default](https://github.com/scicloj/kindly-default).
(kindly-default/setup!)

(comment
  ;; Start Clay.
  (clay/start!)

  ;; Configure Clay for slides
  (clay/swap-options!
   assoc
   :quarto {:format
            {:revealjs {:theme :solarized
                        :navigation-mode :vertical
                        :transition :slide
                        :background-transition :fade
                        :embed-resources true
                        :highlight-style :solarized}}})

  ;; Configure Clay for docs
  (clay/swap-options!
   assoc
   :quarto {:format
            {:html {:toc true
                    :theme :lux
                    :highlight-style :solarized
                    :embed-resources true}}})

  ;; Reset Clay configuration
  (clay/reset-options!))

(comment
  ;; start Clerk
  (clerk/serve! {:browse? true}))
