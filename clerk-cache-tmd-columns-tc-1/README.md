
This experiment records an interesting behaviour that emerges in a certain combination of [Clerk's](https://github.com/nextjournal/clerk) cache and [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset)'s [Columns](https://github.com/techascent/tech.ml.dataset/blob/master/src/tech/v3/dataset/impl/column.clj).

(The project name hints at [Tablecloth](https://github.com/scicloj/tablecloth) too, but it turns out it is irrelevant to the situation.)

The behaviour was discovered by Ethan Miller in his work on Tablecloth's upcoming column API together with [Clay](https://github.com/scicloj/clay). After various explorations we have reached this simpler demonstration of the phenomenon, where Clay is not involved.

The [explanation](https://github.com/scicloj/visual-tools-experiments/tree/main/clerk-cache-tmd-columns-tc-1#explanation) is related to the way Columns are serialized/deserialized by [Nippy](https://github.com/ptaoussanis/nippy).

## Brief description

When loaded from Clerk's cache on disk, Columns are displayed differently if the `tech.v3.dataset` is `require`d.

## Process

1. Run the REPL once, and show the notebook:

```clj
user> (require 'nextjournal.clerk)
nil
user> (nextjournal.clerk/serve! {})
Clerk webserver started on 7777...
{}
user> (nextjournal.clerk/show! "src/notebook.clj")
Clerk evaluated 'src/notebook.clj' in 6012.471483ms.
nil
notebook>
```

2. Restart the REPL once, and show the notebook again (by the same process).

3. Clear Clerk's cache, and show the notebook again.

```clj
notebook> (nextjournal.clerk/clear-cache!)
:cache-dir/deleted ".clerk/cache"
nil
notebook> (nextjournal.clerk/show! "src/notebook.clj")
Clerk evaluated 'src/notebook.clj' in 82.630991ms.
nil
notebook> 
```

## Results

In 1, 3, the Column created in `srcnotebook.clj` is displayed as a list:

```clj
(0 1 2 3)
```

In 2, it is displayed as a map, which looks like a map representation of the tech.ml.dataset Column datatype:

```clj
{:tech.v3.dataset/data #array-buffer<int64>[4] [0, 1, 2, 3] 
 :tech.v3.dataset/force-datatype? true 
 :tech.v3.dataset/missing {} 
 :tech.v3.dataset/name nil}
```

## Variation

Interestingly, if we remove `tech.v3.dataset` from the `require`d namespaces at `src/notebook.clj`, then we do not experience this difference.

## Explanation

The current (version "6.085") tech.ml.dataset serialization/deserialization of Columns seems broken:

```clj
$ clj -Sdeps '{:deps {techascent/tech.ml.dataset {:mvn/version "6.085"}}}'
Clojure 1.10.3

user=> (require '[taoensso.nippy :as nippy]
         '[tech.v3.dataset]
         '[tech.v3.dataset.column :as column])

nil

user=> (-> (range 4)
    column/new-column
    nippy/freeze
    nippy/thaw)

#:tech.v3.dataset{:name nil, :missing {}, :force-datatype? true, :data #array-buffer<int64>[4]
[0, 1, 2, 3]}

user=> (-> (range 4)
    column/new-column
    nippy/freeze
    nippy/thaw
    type)

clojure.lang.PersistentArrayMap



```

(In the variation above, when we do not require the `tech.v3.dataset` namespace, we avoid this Nippy behaviour, and under Clerk, would get some default Nippy behaviour that behaves differently.)

Created an issue: https://github.com/techascent/tech.ml.dataset/issues/298
