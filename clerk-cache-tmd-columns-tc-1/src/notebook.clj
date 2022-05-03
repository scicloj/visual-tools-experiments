(ns notebook
  (:require [nextjournal.clerk :as clerk]
            [tech.v3.dataset.column :as column]
            [tech.v3.dataset]))

(column/new-column (range 4))
