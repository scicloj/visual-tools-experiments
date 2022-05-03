(ns notebook
  (:require [nextjournal.clerk :as clerk]
            [tech.v3.dataset.column :as column]
            [tablecloth.api :as tc]))

(comment
  (clerk/serve! {}))

(column/new-column (range 4))
