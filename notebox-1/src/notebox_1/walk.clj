(ns notebox-1.walk
  (:require tech.v3.dataset))

;; A dataset-friendly version of clojure.walk

(defn walk
  [inner outer form]
  (-> (cond
        (instance? tech.v3.dataset.impl.column.Column (outer form))
        form
        ;;
        (instance? tech.v3.dataset.impl.dataset.Dataset (outer form))
        form
        ;;
        (list? form)
        (outer (apply list (map inner form)))
        ;;
        (instance? clojure.lang.IMapEntry form)
        (outer (clojure.lang.MapEntry/create (inner (key form)) (inner (val form))))
        ;;
        (seq? form) (outer (doall (map inner form)))
        ;;
        (instance? clojure.lang.IRecord form)
        (outer (reduce (fn [r x] (conj r (inner x))) form form))
        ;;
        (coll? form)
        (outer (into (empty form) (map inner form)))
        ;;
        :else (outer form))))

(defn postwalk
  [f form]
  (walk (partial postwalk f) f form))

(defn prewalk
  [f form]
  (walk (partial prewalk f) identity (f form)))
