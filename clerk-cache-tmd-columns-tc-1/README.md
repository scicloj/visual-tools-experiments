
This experiment records an interesting behaviour that emerges in a certain combination of [Clerk's](https://github.com/nextjournal/clerk) cache, [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset)'s [Columns](https://github.com/techascent/tech.ml.dataset/blob/master/src/tech/v3/dataset/impl/column.clj), and [Tablecloth](https://github.com/scicloj/tablecloth).

The behaviour was discovered by Ethan Miller on his work on Tablecloth's upcoming column API together with [Clay](https://github.com/scicloj/clay). After various explorations we have reached this simpler demonstration of the phenomenon, where Clay is not involved.

## Brief description

When loaded from Clerk's cache on disk, Columns are displayed differently if the Tablecloth API is `require`d.

## Process

1. Run the REPl once, and show the notebook:

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

2. Restart the REPl once, and show the notebook again (by the same process).

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

## Experienced results

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

Interestingly, if we remove `tablecloth.api` from the `require`d namespaces at `src/notebook.clj`, then we do not experience this difference.

